package com.cts.inventorymanagement.stock.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReorderLevelUpdateRequest {

    @NotNull(message = "Reorder level cannot be null")
    @Min(value = 0, message = "Reorder level cannot be negative")
    private Integer reorderLevel;
}