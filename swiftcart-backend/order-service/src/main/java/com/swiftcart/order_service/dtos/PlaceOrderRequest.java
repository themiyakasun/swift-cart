package com.swiftcart.order_service.dtos;

import lombok.Data;

@Data
public class PlaceOrderRequest {
    private Long userId;
    private String fullName;
    private String phoneNumber;
    private AddressDto shippingAddress;
    private String paymentMethod;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AddressDto getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(AddressDto shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Data
    public static class AddressDto {
        private String addressline1;
        private String street;
        private String city;
        private String postalCode;
        private String country;

        public String getFormattedAddress() {
            return addressline1 + "," + street + ", " + city + ", " + postalCode + ", " + country;
        }
    }
}