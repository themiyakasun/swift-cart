package com.swiftcart.payment_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class PaymentResponseDto {
    private String clientSecret;

    public PaymentResponseDto() {
    }

    public PaymentResponseDto(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
