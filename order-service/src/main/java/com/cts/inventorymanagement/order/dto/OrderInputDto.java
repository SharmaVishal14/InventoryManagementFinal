package com.cts.inventorymanagement.order.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderInputDto {
    @NotNull
    private Long customerId;
    
    @NotEmpty
    private List<OrderItemInputDto> items;
}
