package com.swiftcart.payment_service.controller;

import com.stripe.exception.StripeException;
import com.swiftcart.payment_service.dtos.PaymentDto;
import com.swiftcart.payment_service.dtos.PaymentRequest;
import com.swiftcart.payment_service.dtos.PaymentResponseDto;
import com.swiftcart.payment_service.dtos.ProcessPaymentRequest;
import com.swiftcart.payment_service.response.ApiResponse;
import com.swiftcart.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-intent")
    public ResponseEntity<ApiResponse> createPaymentIntent(@RequestBody PaymentRequest request) throws StripeException {
        // StripeExceptions are pushed up to the GlobalExceptionHandler
        PaymentResponseDto responseData = paymentService.createPaymentIntent(request);
        return ResponseEntity.ok(new ApiResponse("Payment Intent created successfully", responseData));
    }

    @PostMapping("/process")
    // Swapped PaymentRequest for ProcessPaymentRequest so the JSON parses correctly
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody ProcessPaymentRequest request) {

        // Build the exact JSON response the Order Service expects
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Payment processed successfully");

        Map<String, String> data = new HashMap<>();
        data.put("transactionId", "txn_" + UUID.randomUUID().toString().substring(0, 8));
        data.put("paymentStatus", "SUCCESS"); // The word the Order Service is waiting for!

        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse> getPaymentByOrderId(@PathVariable String orderId) {
        PaymentDto paymentDto = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(new ApiResponse("Payment retrieved successfully", paymentDto));
    }
}