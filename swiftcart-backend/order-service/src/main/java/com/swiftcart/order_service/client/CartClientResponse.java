package com.swiftcart.order_service.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartClientResponse {
    private CartData data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CartData {
        private List<CartItemDto> items;
        private BigDecimal totalCartValue;

        public List<CartItemDto> getItems() {
            return items;
        }

        public void setItems(List<CartItemDto> items) {
            this.items = items;
        }

        public BigDecimal getTotalCartValue() {
            return totalCartValue;
        }

        public void setTotalCartValue(BigDecimal totalCartValue) {
            this.totalCartValue = totalCartValue;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CartItemDto {
        private Long productId;
        private Integer quantity;
        private BigDecimal unitPrice;

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }
    }

    public CartData getData() {
        return data;
    }

    public void setData(CartData data) {
        this.data = data;
    }
}