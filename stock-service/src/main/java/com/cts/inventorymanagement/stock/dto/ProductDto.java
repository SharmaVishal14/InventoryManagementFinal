package com.cts.inventorymanagement.stock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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
        OUT_OF_STOCK,
        DELETED
    }
}