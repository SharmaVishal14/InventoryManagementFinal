package com.cts.inventorymanagement.product.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockUpdateRequest {
    @NotNull
    @Positive
    private Integer quantity;
    
    @NotNull
    private Operation operation;
    
    public enum Operation {
        INCREMENT, DECREMENT
    }
}