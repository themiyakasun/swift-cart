package com.swiftcart.payment_service.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.swiftcart.payment_service.dtos.PaymentRequest;
import com.swiftcart.payment_service.dtos.PaymentResponseDto;
import com.swiftcart.payment_service.model.Payment;
import com.swiftcart.payment_service.model.PaymentStatus;
import com.swiftcart.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.swiftcart.payment_service.dtos.PaymentDto;
import com.swiftcart.payment_service.exceptions.PaymentNotFoundException;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentResponseDto createPaymentIntent(PaymentRequest request) throws StripeException {
        // 1. Ask Stripe to create a Payment Intent
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(request.getAmount())
                        .setCurrency(request.getCurrency())
                        .setReceiptEmail(request.getReceiptEmail())
                        .putMetadata("orderId", request.getOrderId()) // Good practice to send your order ID to Stripe
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        PaymentIntent intent = PaymentIntent.create(params);

        // 2. Save the PENDING payment to our local database
        Payment payment = new Payment();
        payment.setStripePaymentIntentId(intent.getId());
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setStatus(PaymentStatus.PENDING);

        paymentRepository.save(payment);

        // 3. Return the secret using an empty constructor and setter
        PaymentResponseDto response = new PaymentResponseDto();
        response.setClientSecret(intent.getClientSecret());
        return response;
    }

    public PaymentDto getPaymentByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order ID: " + orderId));

        // Convert Entity to DTO using the Builder
        PaymentDto dto = new PaymentDto();
        dto.setOrderId(payment.getOrderId());
        dto.setStripePaymentIntentId(payment.getStripePaymentIntentId());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setStatus(payment.getStatus());
        dto.setCreatedAt(payment.getCreatedAt());

        return dto;
    }
}