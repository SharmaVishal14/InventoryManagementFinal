package com.cts.inventorymanagement.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cts.inventorymanagement.order.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.productId = :productId")
    List<Order> findByProductId(@Param("productId") Long productId);
    
    List<Order> findByCustomerId(Long customerId);
}