package com.cts.inventorymanagement.supplier.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long productId;
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;
    
    private String imageUrl;
    
    private ProductCategory category;
    
    private ProductStatus status;
    
    public enum ProductCategory{
    	ELECTRONICS,
    	CLOTHING,
    	ESSENTIALS
    }
    
    public enum ProductStatus {
        ACTIVE, 
        DISCONTINUED, 
        OUT_OF_STOCK
    }
}