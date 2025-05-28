package com.cts.inventorymanagement.purchase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import com.cts.inventorymanagement.purchase.model.SupplierStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDetailsResponse {
    private Long id;
    private String name;
    private com.cts.inventorymanagement.purchase.model.ContactInfo contactInfo; 
    private List<ProductDto> productsSuppliedDetails;
    private SupplierStatus status; // Added status
}