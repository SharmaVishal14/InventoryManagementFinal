package com.cts.inventorymanagement.supplier.service;

import com.cts.inventorymanagement.supplier.dto.*;
import java.time.LocalDate;
import java.util.List;

public interface SupplierService {
    List<SupplierResponse> getAllSuppliers();
    SupplierResponse createSupplier(SupplierRequest request);
    SupplierDetailsResponse getSupplierDetails(Long id);
    List<PurchaseOrderDto> getSupplierOrdersBetweenDates(Long supplierId, LocalDate startDate, LocalDate endDate);
    List<SupplierResponse> getSuppliersByProduct(Long productId);
    SupplierResponse updateSupplierContact(Long id, ContactUpdateRequest request);
    SupplierResponse updateSupplierStatus(Long id, SupplierStatusUpdateRequest request);
}