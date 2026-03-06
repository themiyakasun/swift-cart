package com.swiftcart.order_service.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShippingClientResponse {
    private String message;
    private ShippingData data;

    public ShippingClientResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ShippingData getData() {
        return data;
    }

    public void setData(ShippingData data) {
        this.data = data;
    }

    // Nested class to catch the data payload
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ShippingData {
        private String trackingNumber;
        private String shippingStatus;

        public ShippingData() {
        }

        public String getTrackingNumber() {
            return trackingNumber;
        }

        public void setTrackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
        }

        public String getShippingStatus() {
            return shippingStatus;
        }

        public void setShippingStatus(String shippingStatus) {
            this.shippingStatus = shippingStatus;
        }
    }
}
