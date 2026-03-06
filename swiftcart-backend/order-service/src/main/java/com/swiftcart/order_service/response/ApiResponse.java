package com.swiftcart.order_service.response;

import lombok.Data;

// Every single request returns the exact same format.
@Data // automatically generates Getters, Setters, toString(), equals(), and hashCode()
public class ApiResponse<T> {
    private String message; // holds a human-readable status message
    private T data; // hold anything

    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }
}
