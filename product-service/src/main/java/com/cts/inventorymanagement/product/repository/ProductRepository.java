package com.cts.inventorymanagement.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.inventorymanagement.product.model.Product;
import com.cts.inventorymanagement.product.model.Product.ProductStatus;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatusNot(ProductStatus status);
    List<Product> findByNameContainingIgnoreCaseAndStatusNot(String name, ProductStatus status);
    List<Product> findByStatus(ProductStatus status);
    List<Product> findByCategory(Product.ProductCategory category);
	boolean existsByProductIdAndStatusNot(Long productId, ProductStatus deleted);
}