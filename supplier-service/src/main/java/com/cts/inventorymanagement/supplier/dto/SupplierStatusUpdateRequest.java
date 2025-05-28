package com.cts.inventorymanagement.supplier.dto;

import com.cts.inventorymanagement.supplier.model.SupplierStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierStatusUpdateRequest {
    @NotNull(message = "Status cannot be null")
    private SupplierStatus status;
}