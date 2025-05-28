package com.cts.inventorymanagement.purchase.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PurchaseOrderRequest {
    @NotNull @Positive
    private Long supplierId;
    
    @NotNull @Positive
    private Long productId;
    
    @NotNull @Min(1)
    private Integer quantity;
    
    @FutureOrPresent
    private LocalDate orderDate = LocalDate.now();
    
    @Future
    private LocalDate deliveryDate;
}