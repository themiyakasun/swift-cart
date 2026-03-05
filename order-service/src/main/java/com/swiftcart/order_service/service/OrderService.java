package com.swiftcart.order_service.service;

import com.swiftcart.order_service.client.*;
import com.swiftcart.order_service.dtos.OrderResponse;
import com.swiftcart.order_service.dtos.PlaceOrderRequest;
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

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, CartClient cartClient, InventoryClient inventoryClient, PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.cartClient = cartClient;
        this.inventoryClient = inventoryClient;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        // 1. Fetch Cart
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

        // Uses the helper method to format the nested JSON into a single string!
        order.setShippingAddress(request.getShippingAddress().getFormattedAddress());
        order.setTotalAmount(totalAmount);

        // Use the Enum!
        order.setStatus(OrderStatus.PENDING);

        // Map Cart Items to Order Items & calculate prices
        for (CartClientResponse.CartItemDto cartItem : cartResponse.getData().getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());

            // Calculate item total: (quantity * unitPrice) - discount
            BigDecimal itemDiscount = BigDecimal.ZERO; // Set to zero by default for now
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
                order.setStatus(OrderStatus.CANCELLED); // Enum!
                orderRepository.save(order);
                throw new RuntimeException("Payment failed!");
            }
        } catch (Exception e) {
            order.setStatus(OrderStatus.CANCELLED); // Enum!
            orderRepository.save(order);

            System.out.println("PAYMENT FEIGN ERROR: " + e.getMessage());
            e.printStackTrace();

            // Updates the Postman response to show the real error
            throw new RuntimeException("Payment Service error: " + e.getMessage());
        }

        // Payment Success -> Deduct Inventory
        for (OrderItem item : order.getItems()) {
            inventoryClient.reduceInventory(new InventoryReduceRequest(item.getProductId(), item.getQuantity()));
        }

        // Update Order to CONFIRMED & Clear Cart
        order.setStatus(OrderStatus.CONFIRMED); // Enum!
        orderRepository.save(order);
        cartClient.clearCart(request.getUserId());

        return orderMapper.toResponse(order);
    }
}