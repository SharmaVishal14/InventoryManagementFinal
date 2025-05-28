package com.cts.inventorymanagement.order.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.cts.inventorymanagement.order.model.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderOutputDto {
    private Long id;
    private Long customerId;
    private List<OrderItemOutputDto> items;
    private LocalDateTime orderdate;
    private Order.Status orderStatus;
    private BigDecimal totalPrice;
}