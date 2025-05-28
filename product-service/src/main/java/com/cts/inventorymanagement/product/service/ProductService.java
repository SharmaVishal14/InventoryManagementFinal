package com.cts.inventorymanagement.product.service;

import com.cts.inventorymanagement.product.dto.ProductDto;
import com.cts.inventorymanagement.product.dto.StockDto;
import com.cts.inventorymanagement.product.model.Product.ProductStatus;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto);
    ProductDto getProductById(Long id);
    List<ProductDto> getAllProducts();
    ProductDto updateProduct(Long id, ProductDto productDto);
    void deleteProduct(Long id);
    List<ProductDto> searchProductsByName(String name);
    List<ProductDto> getLowStockProducts();
    StockDto updateStockLevel(Long productId, int quantity);
    StockDto getStock(Long productId);
    ProductDto updateProductStatus(Long productId,ProductStatus status);
}