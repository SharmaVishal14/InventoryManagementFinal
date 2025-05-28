package com.cts.inventorymanagement.stock.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.cts.inventorymanagement.stock.dto.ProductDto;
import com.cts.inventorymanagement.stock.dto.ProductDto.ProductStatus;

@FeignClient(name = "product-service")
public interface ProductClient {
    @PatchMapping(value = "/products/status/{id}", consumes = "application/json")
    void updateProductStatus(
        @PathVariable("id") Long productId, 
        @RequestParam("status") ProductStatus status
    );
}