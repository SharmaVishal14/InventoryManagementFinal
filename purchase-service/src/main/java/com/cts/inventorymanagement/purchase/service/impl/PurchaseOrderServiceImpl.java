package com.cts.inventorymanagement.purchase.service.impl;

import com.cts.inventorymanagement.purchase.dto.PurchaseOrderDto;
import com.cts.inventorymanagement.purchase.dto.PurchaseOrderRequest;
import com.cts.inventorymanagement.purchase.model.OrderStatus;
import com.cts.inventorymanagement.purchase.model.PurchaseOrder; // Assuming this is the JPA entity
import com.cts.inventorymanagement.purchase.repository.PurchaseOrderRepository;
import com.cts.inventorymanagement.purchase.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger; // Import for Logger
import org.slf4j.LoggerFactory; // Import for LoggerFactory

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    // Initialize the logger for this class
    private static final Logger log = LoggerFactory.getLogger(PurchaseOrderServiceImpl.class);

    private final PurchaseOrderRepository repository;

    @Override
    @Transactional
    public PurchaseOrderDto createOrder(PurchaseOrderRequest request) {
        log.info("Attempting to create a new purchase order for supplier ID: {} and product ID: {}",
                 request.getSupplierId(), request.getProductId());
        PurchaseOrder order = new PurchaseOrder();
        order.setSupplierId(request.getSupplierId());
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setOrderDate(request.getOrderDate());
        order.setDeliveryDate(request.getDeliveryDate());

        try {
            PurchaseOrder savedOrder = repository.save(order);
            log.debug("Purchase order saved to database with ID: {}", savedOrder.getId());
            log.info("Successfully created purchase order with ID: {}", savedOrder.getId());
            return convertToDto(savedOrder);
        } catch (Exception e) {
            log.error("Error creating purchase order for supplier ID {} and product ID {}: {}",
                      request.getSupplierId(), request.getProductId(), e.getMessage(), e);
            throw e; // Re-throw the exception for higher-level handling
        }
    }

    @Override
    public PurchaseOrderDto getOrderById(Long id) {
        log.info("Attempting to retrieve purchase order by ID: {}", id);
        try {
            return convertToDto(repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Purchase order with ID {} not found.", id);
                        return new RuntimeException("Order not found"); // Original exception type
                    }));
        } catch (Exception e) {
            log.error("Error retrieving purchase order by ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<PurchaseOrderDto> getAllOrders() {
        log.info("Attempting to retrieve all purchase orders.");
        try {
            List<PurchaseOrderDto> orders = repository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            log.debug("Retrieved {} purchase orders.", orders.size());
            return orders;
        } catch (Exception e) {
            log.error("Error retrieving all purchase orders: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public PurchaseOrderDto updateOrderStatus(Long id, OrderStatus status) {
        log.info("Attempting to update status for purchase order ID {} to: {}", id, status);
        try {
            PurchaseOrder order = repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Purchase order with ID {} not found for status update.", id);
                        return new RuntimeException("Order not found"); // Original exception type
                    });

            if (status == OrderStatus.DELIVERED) {
                order.setDeliveryDate(LocalDate.now());
                log.debug("Setting delivery date for order ID {} to today as status is DELIVERED.", id);
            }
            order.setStatus(status);
            PurchaseOrder updatedOrder = repository.save(order);
            log.info("Purchase order ID {} status updated successfully to {}.", id, updatedOrder.getStatus());
            return convertToDto(updatedOrder);
        } catch (Exception e) {
            log.error("Error updating status for purchase order ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<PurchaseOrderDto> getOrdersBySupplier(Long supplierId) {
        log.info("Attempting to retrieve purchase orders for supplier ID: {}", supplierId);
        try {
            List<PurchaseOrderDto> orders = repository.findBySupplierId(supplierId).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            log.debug("Found {} purchase orders for supplier ID {}.", orders.size(), supplierId);
            return orders;
        } catch (Exception e) {
            log.error("Error retrieving purchase orders for supplier ID {}: {}", supplierId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<PurchaseOrderDto> getOrdersByProduct(Long productId) {
        log.info("Attempting to retrieve purchase orders for product ID: {}", productId);
        try {
            List<PurchaseOrderDto> orders = repository.findByProductId(productId).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            log.debug("Found {} purchase orders for product ID {}.", orders.size(), productId);
            return orders;
        } catch (Exception e) {
            log.error("Error retrieving purchase orders for product ID {}: {}", productId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<PurchaseOrderDto> getOrdersBetweenDates(LocalDate start, LocalDate end) {
        log.info("Attempting to retrieve purchase orders between dates: {} and {}", start, end);
        try {
            List<PurchaseOrderDto> orders = repository.findByOrderDateBetween(start, end).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            log.debug("Found {} purchase orders between {} and {}.", orders.size(), start, end);
            return orders;
        } catch (Exception e) {
            log.error("Error retrieving purchase orders between {} and {}: {}", start, end, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        log.info("Attempting to delete purchase order with ID: {}", id);
        try {
            repository.deleteById(id);
            log.info("Purchase order with ID {} deleted successfully.", id);
        } catch (Exception e) {
            // Specifically catching EmptyResultDataAccessException if you want to distinguish a not-found delete
            if (e instanceof org.springframework.dao.EmptyResultDataAccessException) {
                log.warn("Attempted to delete non-existent purchase order with ID: {}", id);
            } else {
                log.error("Error deleting purchase order with ID {}: {}", id, e.getMessage(), e);
            }
            throw e;
        }
    }

    private PurchaseOrderDto convertToDto(PurchaseOrder order) {
        // Logging conversion in helper method can be noisy, TRACE is appropriate if needed.
        // log.trace("Converting PurchaseOrder entity with ID {} to PurchaseOrderDto.", order.getId());
        PurchaseOrderDto Dto = new PurchaseOrderDto();
        Dto.setId(order.getId());
        Dto.setSupplierId(order.getSupplierId());
        Dto.setProductId(order.getProductId());
        Dto.setQuantity(order.getQuantity());
        Dto.setOrderDate(order.getOrderDate());
        Dto.setDeliveryDate(order.getDeliveryDate());
        Dto.setStatus(order.getStatus());
        return Dto;
    }
}