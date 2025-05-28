package com.cts.inventorymanagement.supplier.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cts.inventorymanagement.supplier.dto.ProductDto;

@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/products/{id}")
    public ProductDto getProductById(@PathVariable Long id);
}