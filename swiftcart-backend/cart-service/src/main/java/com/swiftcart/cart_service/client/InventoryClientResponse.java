package com.swiftcart.cart_service.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Stops Jackson from crashing!
public class InventoryClientResponse {

    private String message;
    private InventoryData data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InventoryData getData() {
        return data;
    }

    public void setData(InventoryData data) {
        this.data = data;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InventoryData {
        private Integer quantity;
        private Integer availableQuantity;

        // Safely grabs the stock no matter what the Inventory Service named the variable
        public Integer getActualStock() {
            if (availableQuantity != null) return availableQuantity;
            if (quantity != null) return quantity;
            return 0; // Failsafe
        }
    }
}