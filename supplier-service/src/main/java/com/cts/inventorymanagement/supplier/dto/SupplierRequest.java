package com.cts.inventorymanagement.supplier.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.cts.inventorymanagement.supplier.model.ContactInfo;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequest {
    @NotBlank(message = "Supplier name cannot be blank")
    private String name;

    @Valid // Validate the nested ContactInfo object
    private ContactInfo contactInfo;

    @NotEmpty(message = "Products supplied cannot be empty")
    private List<Long> productsSupplied;
}