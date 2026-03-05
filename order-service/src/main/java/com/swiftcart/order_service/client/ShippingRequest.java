package com.swiftcart.order_service.client;

public class ShippingRequest {
    private Long orderId;
    private Long userId;
    private String shippingAddress;

    public ShippingRequest() {
    }

    public ShippingRequest(Long orderId, Long userId, String shippingAddress) {
        this.orderId = orderId;
        this.userId = userId;
        this.shippingAddress = shippingAddress;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
