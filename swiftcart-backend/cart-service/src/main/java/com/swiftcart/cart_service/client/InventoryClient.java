package com.swiftcart.cart_service.client;

import com.swiftcart.cart_service.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service", path = "/inventory")
public interface InventoryClient {

    @GetMapping("/{productId}/stock")
    InventoryClientResponse checkStock(@PathVariable("productId") Long productId);
}
