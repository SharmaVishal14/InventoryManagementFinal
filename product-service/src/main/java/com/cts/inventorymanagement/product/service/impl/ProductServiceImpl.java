package com.cts.inventorymanagement.product.service.impl;

import com.cts.inventorymanagement.product.client.StockClient;
import com.cts.inventorymanagement.product.dto.ProductDto;
import com.cts.inventorymanagement.product.dto.StockDto;
import com.cts.inventorymanagement.product.dto.StockUpdateRequest;
import com.cts.inventorymanagement.product.exception.ProductNotFoundException;
import com.cts.inventorymanagement.product.model.Product;
import com.cts.inventorymanagement.product.model.Product.ProductStatus;
import com.cts.inventorymanagement.product.repository.ProductRepository;
import com.cts.inventorymanagement.product.service.ProductService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private static final int DEFAULT_REORDER_LEVEL = 10;

    private final ProductRepository productRepository;
    private final StockClient stockClient;

    public ProductServiceImpl(ProductRepository productRepository, StockClient stockClient) {
        this.productRepository = productRepository;
        this.stockClient = stockClient;
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        log.info("Creating product: {}", productDto.getName());
        Product product = new Product();
        BeanUtils.copyProperties(productDto, product);
        product.setStatus(ProductStatus.ACTIVE);

        Product savedProduct = null;
        try {
            savedProduct = productRepository.save(product);
            log.debug("Temporarily saved product ID: {}", savedProduct.getProductId());
            
            createInitialStockEntry(savedProduct.getProductId());
            
            log.info("Successfully created product ID: {}", savedProduct.getProductId());
            return convertToDto(savedProduct);
        } catch (Exception e) {
            log.error("Product creation failed for {}: {}", productDto.getName(), e.getMessage());
            
            if (savedProduct != null) {
                log.warn("Rolling back product ID: {}", savedProduct.getProductId());
                deleteProductPermanently(savedProduct.getProductId());
            }
            
            throw new IllegalStateException("Product creation failed: " + e.getMessage(), e);
        }
    }

    private void deleteProductPermanently(Long productId) {
        try {
            productRepository.deleteById(productId);
            log.info("Permanently deleted product ID: {}", productId);
        } catch (Exception e) {
            log.error("Failed to delete product ID {}: {}", productId, e.getMessage());
        }
    }

    private void createInitialStockEntry(Long productId) {
        log.info("Creating stock for product ID: {}", productId);
        try {
            StockDto stockDto = new StockDto();
            stockDto.setProductId(productId);
            stockDto.setQuantity(0);
            stockDto.setReorderLevel(DEFAULT_REORDER_LEVEL);
            
            stockClient.addStock(stockDto);
            log.debug("Created stock for product ID: {}", productId);
        } catch (FeignException e) {
            log.error("Stock creation failed for product {}: {}", productId, e.status());
            throw new IllegalStateException("Stock service error: " + e.getMessage());
        }
    }

    @Override
    public ProductDto getProductById(Long id) {
        log.info("Fetching product ID: {}", id);
        return productRepository.findById(id)
                .filter(p -> p.getStatus() != ProductStatus.DELETED)
                .map(product -> {
                    log.debug("Found product ID {}: {}", id, product.getName());
                    return convertToDto(product);
                })
                .orElseThrow(() -> {
                    log.error("Product ID {} not found", id);
                    return new ProductNotFoundException("Product not found with id: " + id);
                });
    }

    @Override
    public List<ProductDto> getAllProducts() {
        log.info("Fetching all active products");
        return productRepository.findByStatusNot(ProductStatus.DELETED)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        log.info("Updating product ID: {}", id);
        return productRepository.findById(id)
                .filter(p -> p.getStatus() != ProductStatus.DELETED)
                .map(existing -> {
                    BeanUtils.copyProperties(productDto, existing);
                    existing.setProductId(id);
                    Product updated = productRepository.save(existing);
                    log.info("Updated product ID: {}", id);
                    return convertToDto(updated);
                })
                .orElseThrow(() -> {
                    log.error("Update failed - product ID {} not found", id);
                    return new ProductNotFoundException("Product not found with id: " + id);
                });
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("Soft deleting product ID: {}", id);
        productRepository.findById(id)
                .ifPresentOrElse(
                        product -> {
                            if (product.getStatus() == ProductStatus.DELETED) {
                                log.warn("Product ID {} already deleted", id);
                                return;
                            }
                            product.setStatus(ProductStatus.DELETED);
                            productRepository.save(product);
                            log.info("Soft deleted product ID: {}", id);
                        },
                        () -> {
                            log.error("Delete failed - product ID {} not found", id);
                            throw new ProductNotFoundException("Product not found with id: " + id);
                        }
                );
    }

    @Override
    public ProductDto updateProductStatus(Long productId, ProductStatus newStatus) {
        log.info("Updating status for product ID {} to {}", productId, newStatus);
        return productRepository.findById(productId)
                .filter(p -> p.getStatus() != ProductStatus.DELETED)
                .map(product -> {
                    if (product.getStatus() == newStatus) {
                        log.warn("Product ID {} already has status {}", productId, newStatus);
                        return convertToDto(product);
                    }
                    product.setStatus(newStatus);
                    Product updated = productRepository.save(product);
                    log.info("Updated status for product ID {} to {}", productId, newStatus);
                    return convertToDto(updated);
                })
                .orElseThrow(() -> {
                    log.error("Status update failed - product ID {} not found", productId);
                    return new ProductNotFoundException("Product not found with id: " + productId);
                });
    }

    @Override
    public List<ProductDto> searchProductsByName(String name) {
        log.info("Searching products by name: {}", name);
        return productRepository.findByNameContainingIgnoreCaseAndStatusNot(name, ProductStatus.DELETED)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getLowStockProducts() {
        log.info("Fetching low stock products");
        try {
            return stockClient.getLowStockItems().stream()
                    .map(stock -> {
                        try {
                            return getProductById(stock.getProductId());
                        } catch (ProductNotFoundException e) {
                            log.warn("Missing product for stock ID {}", stock.getProductId());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (FeignException e) {
            log.error("Low stock fetch failed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public StockDto updateStockLevel(Long productId, int quantity) {
        log.info("Updating stock for product ID {} by {}", productId, quantity);
        validateProductExists(productId);

        StockUpdateRequest.Operation operation = quantity > 0 
            ? StockUpdateRequest.Operation.INCREMENT 
            : StockUpdateRequest.Operation.DECREMENT;

        StockUpdateRequest request = new StockUpdateRequest(
            Math.abs(quantity),
            operation
        );

        try {
            StockDto updated = stockClient.updateStock(productId, request);
            log.info("Stock updated for product ID {}: {}", productId, updated.getQuantity());
            return updated;
        } catch (FeignException e) {
            log.error("Stock update failed for product {}: {}", productId, e.status());
            throw new IllegalStateException("Stock update failed: " + e.getMessage());
        }
    }

    @Override
    public StockDto getStock(Long productId) {
        log.info("Fetching stock for product ID: {}", productId);
        validateProductExists(productId);
        
        try {
            return stockClient.getStock(productId);
        } catch (FeignException e) {
            log.error("Stock fetch failed for product {}: {}", productId, e.status());
            throw new IllegalStateException("Stock fetch failed: " + e.getMessage());
        }
    }

    private void validateProductExists(Long productId) {
        if (!productRepository.existsByProductIdAndStatusNot(productId, ProductStatus.DELETED)) {
            log.error("Validation failed for product ID: {}", productId);
            throw new ProductNotFoundException("Product not found with id: " + productId);
        }
    }

    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        BeanUtils.copyProperties(product, dto);
        return dto;
    }
}