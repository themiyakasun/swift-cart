package com.swiftcart.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cart-service", path = "/cart")
public interface CartClient {
    @GetMapping("/{userId}")
    CartClientResponse getCartByUserId(@PathVariable("userId") Long userId);

    @DeleteMapping("/{userId}/clear")
    void clearCart(@PathVariable("userId") Long userId);
}