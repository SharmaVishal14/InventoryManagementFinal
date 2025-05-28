package com.cts.inventorymanagement.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.cts.inventorymanagement.order.dto.StockDto;
import com.cts.inventorymanagement.order.dto.StockUpdateRequest;

@FeignClient(name = "stock-service")
public interface StockClient {
    @GetMapping("/stocks/{productId}")
    StockDto getStock(@PathVariable Long productId);

    @PutMapping("/stocks/{productId}")
    StockDto updateStock(@PathVariable Long productId, @RequestBody StockUpdateRequest request);
}