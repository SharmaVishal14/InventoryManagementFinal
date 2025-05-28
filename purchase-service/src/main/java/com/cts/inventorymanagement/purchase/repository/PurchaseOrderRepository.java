package com.cts.inventorymanagement.purchase.repository;

import com.cts.inventorymanagement.purchase.model.OrderStatus;
import com.cts.inventorymanagement.purchase.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findBySupplierId(Long supplierId);
    List<PurchaseOrder> findByProductId(Long productId);
    List<PurchaseOrder> findByStatus(OrderStatus status);
    
    @Query("SELECT po FROM PurchaseOrder po WHERE po.orderDate BETWEEN :start AND :end")
    List<PurchaseOrder> findByOrderDateBetween(LocalDate start, LocalDate end);
}