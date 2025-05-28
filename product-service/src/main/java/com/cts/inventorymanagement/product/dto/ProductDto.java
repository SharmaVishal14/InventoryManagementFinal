package com.cts.inventorymanagement.product.dto;

import com.cts.inventorymanagement.product.model.Product.ProductCategory;
import com.cts.inventorymanagement.product.model.Product.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
}