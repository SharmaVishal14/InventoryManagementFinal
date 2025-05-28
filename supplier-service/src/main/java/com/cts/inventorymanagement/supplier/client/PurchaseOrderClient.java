package com.cts.inventorymanagement.supplier.client;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.cts.inventorymanagement.supplier.dto.PurchaseOrderDto;

@FeignClient(name = "purchase-service")
public interface PurchaseOrderClient {
    @GetMapping("/suppliers/{supplierId}")
    List<PurchaseOrderDto> getOrdersBySupplier(@PathVariable Long supplierId);
    
    @GetMapping("/suppliers/date-range")
    List<PurchaseOrderDto> getOrdersByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    );
}