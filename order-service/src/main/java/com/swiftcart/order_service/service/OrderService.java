package com.swiftcart.order_service.service;

import com.swiftcart.order_service.client.*;
import com.swiftcart.order_service.dtos.OrderResponse;
import com.swiftcart.order_service.dtos.PlaceOrderRequest;
import com.swiftcart.order_service.dtos.UserResponse;
import com.swiftcart.order_service.mapper.OrderMapper;
import com.swiftcart.order_service.model.Order;
import com.swiftcart.order_service.model.OrderItem;
import com.swiftcart.order_service.model.OrderStatus;
import com.swiftcart.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CartClient cartClient;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;
    private final ShippingClient shippingClient;
    private final NotificationClient notificationClient;
    private final UserClient userClient;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, CartClient cartClient, InventoryClient inventoryClient, PaymentClient paymentClient, ShippingClient shippingClient, NotificationClient notificationClient, UserClient userClient) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.cartClient = cartClient;
        this.inventoryClient = inventoryClient;
        this.paymentClient = paymentClient;
        this.shippingClient = shippingClient;
        this.notificationClient = notificationClient;
        this.userClient = userClient;
    }

    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        // Fetch Cart
        CartClientResponse cartResponse = cartClient.getCartByUserId(request.getUserId());
        if (cartResponse.getData() == null || cartResponse.getData().getItems().isEmpty()) {
            throw new RuntimeException("Cannot place order: Cart is empty!");
        }

        BigDecimal totalAmount = cartResponse.getData().getTotalCartValue();

        // Create PENDING Order
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setFullName(request.getFullName());
        order.setPhoneNumber(request.getPhoneNumber());

        order.setShippingAddress(request.getShippingAddress().getFormattedAddress());
        order.setTotalAmount(totalAmount);

        order.setStatus(OrderStatus.PENDING);

        // Map Cart Items to Order Items & calculate prices
        for (CartClientResponse.CartItemDto cartItem : cartResponse.getData().getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());

            BigDecimal itemDiscount = BigDecimal.ZERO;
            BigDecimal itemTotal = cartItem.getUnitPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()))
                    .subtract(itemDiscount);

            orderItem.setDiscount(itemDiscount);
            orderItem.setTotalPrice(itemTotal);

            order.getItems().add(orderItem);
        }

        order = orderRepository.save(order);

        // Process Payment
        PaymentRequest paymentRequest = new PaymentRequest(order.getId(), request.getUserId(), totalAmount, request.getPaymentMethod());
        try {
            PaymentClientResponse paymentResponse = paymentClient.processPayment(paymentRequest);
            if (!"SUCCESS".equals(paymentResponse.getData().getPaymentStatus())) {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                throw new RuntimeException("Payment failed!");
            }
        } catch (Exception e) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

            System.out.println("PAYMENT FEIGN ERROR: " + e.getMessage());
            e.printStackTrace();

            throw new RuntimeException("Payment Service error: " + e.getMessage());
        }

        // Payment Success -> Deduct Inventory
        for (OrderItem item : order.getItems()) {
            inventoryClient.reduceInventory(new InventoryReduceRequest(item.getProductId(), item.getQuantity()));
        }

        // Update Order to CONFIRMED & Clear Cart
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
        cartClient.clearCart(request.getUserId());

        // Request Shipping for the confirmed order
        String trackingNumber = "Pending Generation";
        try {
            ShippingRequest shippingRequest = new ShippingRequest(
                    order.getId(),
                    request.getUserId(),
                    order.getShippingAddress()
            );

            ShippingClientResponse shippingResponse = shippingClient.createShipment(shippingRequest);
            trackingNumber = shippingResponse.getData().getTrackingNumber();
            System.out.println("Shipment created successfully! Tracking: " + trackingNumber);

        } catch (Exception e) {
            System.out.println("SHIPPING FEIGN ERROR: Could not create shipment right now. Error: " + e.getMessage());
        }

        // Send Order Confirmation Notification
        try {
            // Get User Email
            UserResponse user = userClient.getUserById(request.getUserId());

            // Build Notification
            NotificationRequest notification = new NotificationRequest();
            notification.setRecipientEmail(user.getEmail());
            notification.setSubject("SwiftCart: Order Confirmed!");
            notification.setMessage("Hi " + user.getUsername() + ",\nYour order " + order.getId() + " is confirmed. Your total was $" + totalAmount + ".\nTracking: " + trackingNumber);
            notification.setType(NotificationType.ORDER_CONFIRMATION);

            // Send Notification
            notificationClient.sendNotification(notification);
            System.out.println("Order confirmation email requested for: " + user.getEmail());

        } catch (Exception e) {
            // Fails silently to prevent order rollback if notification goes down
            System.out.println("NOTIFICATION FEIGN ERROR: Could not send email right now. Error: " + e.getMessage());
        }

        return orderMapper.toResponse(order);
    }
}