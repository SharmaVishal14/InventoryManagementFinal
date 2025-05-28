package com.cts.inventorymanagement.supplier.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.cts.inventorymanagement.supplier.model.Supplier;
import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
    @Query("SELECT s FROM Supplier s WHERE :productId MEMBER OF s.productsSupplied")
    List<Supplier> findByProductsSuppliedContaining(@Param("productId") Long productId);
}
