package com.cts.inventorymanagement.purchase.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cts.inventorymanagement.purchase.dto.SupplierDetailsResponse;

@FeignClient(name = "supplier-service")
public interface SupplierClient {

    @GetMapping("/suppliers/{id}")
    SupplierDetailsResponse getSupplierDetails(@PathVariable("id") Long id);
}