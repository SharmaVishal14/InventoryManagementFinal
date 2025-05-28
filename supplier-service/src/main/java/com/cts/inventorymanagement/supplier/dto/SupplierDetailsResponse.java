package com.cts.inventorymanagement.supplier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.cts.inventorymanagement.supplier.model.ContactInfo;
import com.cts.inventorymanagement.supplier.model.SupplierStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDetailsResponse {
    private Long id;
    private String name;
    private ContactInfo contactInfo; 
    private List<ProductDto> productsSuppliedDetails;
    private SupplierStatus status; // Added status
}