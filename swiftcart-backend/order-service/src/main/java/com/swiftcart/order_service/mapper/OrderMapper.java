package com.swiftcart.order_service.mapper;

import com.swiftcart.order_service.dtos.OrderResponse;
import com.swiftcart.order_service.model.Order;
import com.swiftcart.order_service.model.OrderItem;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    public OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setFullName(order.getFullName());
        response.setShippingAddress(order.getShippingAddress());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());

        response.setItems(order.getItems().stream().map((OrderItem item) -> {
            OrderResponse.OrderItemResponse itemDto = new OrderResponse.OrderItemResponse();
            itemDto.setProductId(item.getProductId());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setUnitPrice(item.getUnitPrice());
            itemDto.setDiscount(item.getDiscount());
            itemDto.setTotalPrice(item.getTotalPrice());
            return itemDto;
        }).collect(Collectors.toList()));

        return response;
    }
}