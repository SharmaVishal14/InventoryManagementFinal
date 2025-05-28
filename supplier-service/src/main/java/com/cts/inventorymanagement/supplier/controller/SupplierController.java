package com.cts.inventorymanagement.supplier.controller;

import com.cts.inventorymanagement.supplier.dto.*;
import com.cts.inventorymanagement.supplier.service.SupplierService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate; 
import java.util.List;

@RestController
@RequestMapping("/suppliers") // Added a base path for consistency
public class SupplierController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierController.class);
    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ResponseEntity<List<SupplierResponse>> getAllSuppliers() {
        logger.info("GET /suppliers");
        List<SupplierResponse> suppliers = supplierService.getAllSuppliers();
        logger.debug("Returning {} suppliers.", suppliers.size());
        return new ResponseEntity<>(suppliers, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SupplierResponse> createSupplier(
            @Valid @RequestBody SupplierRequest request) {
        logger.info("POST /suppliers - Request body: {}", request);
        SupplierResponse createdSupplier = supplierService.createSupplier(request);
        logger.info("Supplier created with ID: {}", createdSupplier.getId());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdSupplier.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdSupplier);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDetailsResponse> getSupplierDetails(
            @PathVariable Long id) {
        logger.info("GET /suppliers/{}", id);
        SupplierDetailsResponse supplierDetails = supplierService.getSupplierDetails(id);
        logger.debug("Returning details for supplier ID: {}", id);
        return ResponseEntity.ok(supplierDetails);
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<List<PurchaseOrderDto>> getSupplierOrders(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        logger.info("GET /suppliers/{}/orders - start: {}, end: {}", id, start, end);
        List<PurchaseOrderDto> orders = supplierService.getSupplierOrdersBetweenDates(id, start, end);
        logger.debug("Returning {} orders for supplier ID: {} between {} and {}.", orders.size(), id, start, end);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<List<SupplierResponse>> getSuppliersByProduct(
            @PathVariable Long productId) {
        logger.info("GET /suppliers/by-product/{}", productId);
        List<SupplierResponse> suppliers = supplierService.getSuppliersByProduct(productId);
        logger.debug("Returning {} suppliers for product ID: {}.", suppliers.size(), productId);
        return ResponseEntity.ok(suppliers);
    }

    // New endpoint to update supplier contact information
    @PatchMapping("/{id}/contact")
    public ResponseEntity<SupplierResponse> updateSupplierContact(
            @PathVariable Long id,
            @Valid @RequestBody ContactUpdateRequest request) {
        logger.info("PATCH /suppliers/{}/contact - Request body: {}", id, request);
        SupplierResponse updatedSupplier = supplierService.updateSupplierContact(id, request);
        logger.info("Contact info updated for supplier ID: {}", id);
        return ResponseEntity.ok(updatedSupplier);
    }

    // New endpoint to update supplier status
    @PatchMapping("/{id}/status")
    public ResponseEntity<SupplierResponse> updateSupplierStatus(
            @PathVariable Long id,
            @Valid @RequestBody SupplierStatusUpdateRequest request) {
        logger.info("PATCH /suppliers/{}/status - Request body: {}", id, request);
        SupplierResponse updatedSupplier = supplierService.updateSupplierStatus(id, request);
        logger.info("Status updated for supplier ID: {} to {}", id, request.getStatus());
        return ResponseEntity.ok(updatedSupplier);
    }
}