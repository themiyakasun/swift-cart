package com.swiftcart.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", path = "/inventory")
public interface InventoryClient {
    @PutMapping("/reduce")
    void reduceInventory(@RequestBody InventoryReduceRequest request);
}