package com.cts.inventorymanagement.stock.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
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