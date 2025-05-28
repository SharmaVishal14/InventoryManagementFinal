package com.cts.inventorymanagement.supplier.dto;

import lombok.Data;
import java.time.LocalDate;

import com.cts.inventorymanagement.supplier.model.OrderStatus;

@Data
public class PurchaseOrderDto {
    private Long id;
    private Long supplierId;
    private Long productId;
    private Integer quantity;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private OrderStatus status;
}
