package com.swiftcart.order_service.controller;

import com.swiftcart.order_service.dtos.PlaceOrderRequest;
import com.swiftcart.order_service.dtos.OrderResponse;
import com.swiftcart.order_service.response.ApiResponse;
import com.swiftcart.order_service.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(@RequestBody PlaceOrderRequest request) {
        OrderResponse response = orderService.placeOrder(request);
        return ResponseEntity.ok(new ApiResponse<>("Order placed successfully!", response));
    }
}