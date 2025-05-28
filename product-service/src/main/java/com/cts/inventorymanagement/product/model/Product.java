package com.cts.inventorymanagement.product.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;
    
    private String imageUrl;
    
    @Enumerated(EnumType.STRING)
    private ProductCategory category;
    
    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.ACTIVE;
    
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