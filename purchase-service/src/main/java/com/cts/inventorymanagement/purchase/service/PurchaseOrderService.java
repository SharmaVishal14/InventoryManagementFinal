package com.cts.inventorymanagement.purchase.service;

import com.cts.inventorymanagement.purchase.dto.PurchaseOrderDto;
import com.cts.inventorymanagement.purchase.dto.PurchaseOrderRequest;
import com.cts.inventorymanagement.purchase.model.*;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrderDto createOrder(PurchaseOrderRequest request);
    PurchaseOrderDto getOrderById(Long orderId);
    List<PurchaseOrderDto> getAllOrders();
    PurchaseOrderDto updateOrderStatus(Long orderId, OrderStatus status);
    List<PurchaseOrderDto> getOrdersBySupplier(Long supplierId);
    List<PurchaseOrderDto> getOrdersByProduct(Long productId);
    List<PurchaseOrderDto> getOrdersBetweenDates(LocalDate start, LocalDate end);
    void deleteOrder(Long orderId);
}
