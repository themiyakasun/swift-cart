package com.swiftcart.cart_service.client;

public record InventoryResponse(
        Long productId,
        Integer quantity,
        boolean isInStock
) {}