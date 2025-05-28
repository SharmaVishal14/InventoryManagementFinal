package com.cts.inventorymanagement.supplier.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cts.inventorymanagement.supplier.client.ProductClient;
import com.cts.inventorymanagement.supplier.client.PurchaseOrderClient;
import com.cts.inventorymanagement.supplier.exceptions.ProductNotFoundException;
import com.cts.inventorymanagement.supplier.exceptions.SupplierNotFoundException;
import com.cts.inventorymanagement.supplier.model.ContactInfo;
import com.cts.inventorymanagement.supplier.model.Supplier;
import com.cts.inventorymanagement.supplier.model.SupplierStatus;
import com.cts.inventorymanagement.supplier.dto.ContactUpdateRequest;
import com.cts.inventorymanagement.supplier.dto.ProductDto;
import com.cts.inventorymanagement.supplier.dto.PurchaseOrderDto;
import com.cts.inventorymanagement.supplier.dto.SupplierDetailsResponse;
import com.cts.inventorymanagement.supplier.dto.SupplierRequest;
import com.cts.inventorymanagement.supplier.dto.SupplierResponse;
import com.cts.inventorymanagement.supplier.dto.SupplierStatusUpdateRequest;
import com.cts.inventorymanagement.supplier.repository.SupplierRepository;
import com.cts.inventorymanagement.supplier.service.SupplierService;

import lombok.RequiredArgsConstructor;
import feign.FeignException;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private static final Logger logger = LoggerFactory.getLogger(SupplierServiceImpl.class);
    private final SupplierRepository repository;
    private final ProductClient productClient;
    private final PurchaseOrderClient purchaseClient;

    @Override
    public List<SupplierResponse> getAllSuppliers() {
        logger.info("Fetching all suppliers.");
        List<Supplier> suppliers = repository.findAll();
        logger.debug("Retrieved {} suppliers.", suppliers.size());
        return suppliers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SupplierResponse createSupplier(SupplierRequest request) {
        logger.info("Attempting to create a new supplier: {}", request.getName());

        if (request.getProductsSupplied() != null && !request.getProductsSupplied().isEmpty()) {
            for (Long productId : request.getProductsSupplied()) {
                logger.debug("Checking existence of product with ID: {}", productId);
                try {
                    productClient.getProductById(productId);
                    logger.debug("Product with ID: {} found.", productId);
                } catch (FeignException.NotFound ex) {
                    logger.error("Product with ID: {} not found in product service. Cannot create supplier.", productId);
                    throw new ProductNotFoundException("Product with ID: " + productId + " not found. Supplier cannot be created.");
                } catch (FeignException ex) {
                    logger.error("Feign client error while checking product ID {}: {}", productId, ex.getMessage());
                    throw new RuntimeException("Error communicating with product service for product ID: " + productId + ". Details: " + ex.getMessage(), ex);
                } catch (Exception ex) {
                    logger.error("Unexpected error while checking product ID {}: {}", productId, ex.getMessage());
                    throw new RuntimeException("An unexpected error occurred during product check for ID: " + productId + ". Details: " + ex.getMessage(), ex);
                }
            }
        } else {
            logger.warn("No products specified for supplier {}.", request.getName());
        }

        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setContactInfo(request.getContactInfo());
        supplier.setProductsSupplied(request.getProductsSupplied());
        supplier.setStatus(SupplierStatus.CURRENT);

        SupplierResponse response = convertToResponse(repository.save(supplier));
        logger.info("Supplier created with ID: {}", response.getId());
        return response;
    }

    @Override
    public SupplierDetailsResponse getSupplierDetails(Long id) {
        logger.info("Fetching details for supplier ID: {}", id);
        Supplier supplier = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Supplier not found with id: {}", id);
                    return new SupplierNotFoundException("Supplier not found with id: " + id);
                });

        logger.debug("Fetching products supplied by supplier ID: {}", id);
        List<ProductDto> products = supplier.getProductsSupplied().stream()
                .map(productId -> {
                    try {
                        return productClient.getProductById(productId);
                    } catch (FeignException.NotFound ex) {
                        logger.warn("Product with ID: {} associated with supplier {} was not found in product service. Excluding from details.", productId, id);
                        return null;
                    } catch (FeignException ex) {
                        logger.error("Feign client error fetching product ID {} for supplier {}: {}", productId, id, ex.getMessage());
                        return null;
                    } catch (Exception ex) {
                        logger.error("Unexpected error fetching product ID {} for supplier {}: {}", productId, id, ex.getMessage());
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
        logger.debug("Retrieved {} products for supplier ID: {}", products.size(), id);

        SupplierDetailsResponse response = new SupplierDetailsResponse(
                supplier.getId(),
                supplier.getName(),
                supplier.getContactInfo(),
                products,
                supplier.getStatus()
        );
        logger.info("Returning details for supplier ID: {}", id);
        return response;
    }

    @Override
    public List<PurchaseOrderDto> getSupplierOrdersBetweenDates(Long supplierId, LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching purchase orders for supplier ID: {} between {} and {}", supplierId, startDate, endDate);
        List<PurchaseOrderDto> allOrders = purchaseClient.getOrdersBySupplier(supplierId);
        List<PurchaseOrderDto> filteredOrders = allOrders.stream()
                .filter(order -> !order.getOrderDate().isBefore(startDate) &&
                                 !order.getOrderDate().isAfter(endDate))
                .collect(Collectors.toList());
        logger.debug("Retrieved {} purchase orders for supplier ID: {} between {} and {}", filteredOrders.size(), supplierId, startDate, endDate);
        return filteredOrders;
    }

    @Override
    public List<SupplierResponse> getSuppliersByProduct(Long productId) {
        logger.info("Fetching suppliers for product ID: {}", productId);
        List<Supplier> suppliers = repository.findByProductsSuppliedContaining(productId);
        logger.debug("Retrieved {} suppliers for product ID: {}", suppliers.size(), productId);
        return suppliers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SupplierResponse updateSupplierContact(Long id, ContactUpdateRequest request) {
        logger.info("Updating contact info for supplier ID: {}", id);
        Supplier supplier = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Supplier not found with id: {} for contact update.", id);
                    return new SupplierNotFoundException("Supplier not found with id: " + id);
                });

        supplier.setContactInfo(new ContactInfo(request.getContactNo(), request.getEmail()));
        SupplierResponse updatedSupplier = convertToResponse(repository.save(supplier));
        logger.info("Contact info updated for supplier ID: {}", id);
        return updatedSupplier;
    }

    @Override
    public SupplierResponse updateSupplierStatus(Long id, SupplierStatusUpdateRequest request) {
        logger.info("Updating status for supplier ID: {} to {}", id, request.getStatus());
        Supplier supplier = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Supplier not found with id: {} for status update.", id);
                    return new SupplierNotFoundException("Supplier not found with id: " + id);
                });

        supplier.setStatus(request.getStatus());
        SupplierResponse updatedSupplier = convertToResponse(repository.save(supplier));
        logger.info("Status updated for supplier ID: {} to {}", id, request.getStatus());
        return updatedSupplier;
    }

    private SupplierResponse convertToResponse(Supplier supplier) {
        SupplierResponse response = new SupplierResponse(
                supplier.getId(),
                supplier.getName(),
                supplier.getContactInfo(),
                supplier.getProductsSupplied(),
                supplier.getStatus()
        );
        logger.debug("Converted Supplier to SupplierResponse. Supplier ID: {}", supplier.getId());
        return response;
    }
}