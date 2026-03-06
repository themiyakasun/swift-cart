package com.swiftcart.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "shipping-service", path = "/shipping")
public interface ShippingClient {
    @PostMapping
    ShippingClientResponse createShipment(@RequestBody ShippingRequest request);
}
