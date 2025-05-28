package com.cts.inventorymanagement.purchase.controller;

import com.cts.inventorymanagement.purchase.dto.PurchaseOrderDto;
import com.cts.inventorymanagement.purchase.dto.PurchaseOrderRequest;
import com.cts.inventorymanagement.purchase.model.OrderStatus;
import com.cts.inventorymanagement.purchase.service.PurchaseOrderService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger; // Import for Logger
import org.slf4j.LoggerFactory; // Import for LoggerFactory

@RestController
@RequestMapping("/purchase-orders") // Added a more specific base path for clarity
public class PurchaseOrderController {

    // Initialize the logger for this class
    private static final Logger log = LoggerFactory.getLogger(PurchaseOrderController.class);

    private final PurchaseOrderService orderService;

    public PurchaseOrderController(PurchaseOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseOrderDto createOrder(@Valid @RequestBody PurchaseOrderRequest request) {
        // Log the incoming request to create a purchase order
        log.info("Received request to create purchase order for supplier ID: {}", request.getSupplierId());
        try {
            PurchaseOrderDto createdOrder = orderService.createOrder(request);
            // Log successful creation
            log.debug("Purchase order created successfully with ID: {}", createdOrder.getId());
            return createdOrder;
        } catch (Exception e) {
            // Log any errors during order creation
            log.error("Error creating purchase order for supplier ID {}: {}", request.getSupplierId(), e.getMessage(), e);
            throw e; // Re-throwing the exception to be handled by a global exception handler if present
        }
    }

    @GetMapping("/{id}")
    public PurchaseOrderDto getOrderById(@PathVariable Long id) {
        // Log the request for a specific purchase order ID
        log.info("Received request to get purchase order by ID: {}", id);
        try {
            PurchaseOrderDto order = orderService.getOrderById(id);
            // Log if order is found or not (assuming service throws exception if not found)
            if (order != null) {
                log.debug("Successfully retrieved purchase order with ID {}.", id);
            } else {
                log.warn("Purchase order with ID {} not found.", id);
            }
            return order;
        } catch (Exception e) {
            // Log errors during retrieval
            log.error("Error fetching purchase order with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping
    public List<PurchaseOrderDto> getAllOrders() {
        // Log the request to get all purchase orders
        log.info("Received request to get all purchase orders.");
        try {
            List<PurchaseOrderDto> orders = orderService.getAllOrders();
            // Log the count of retrieved orders
            log.debug("Successfully retrieved {} purchase orders.", orders.size());
            return orders;
        } catch (Exception e) {
            // Log errors during retrieval of all orders
            log.error("Error fetching all purchase orders: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PatchMapping("/{id}/status")
    public PurchaseOrderDto updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        // Log the request to update order status
        log.info("Received request to update status of purchase order ID {} to: {}", id, status);
        try {
            PurchaseOrderDto updatedOrder = orderService.updateOrderStatus(id, status);
            // Log successful status update
            log.debug("Purchase order ID {} status updated to {}.", id, updatedOrder.getStatus());
            return updatedOrder;
        } catch (Exception e) {
            // Log errors during status update
            log.error("Error updating status for purchase order ID {} to {}: {}", id, status, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/supplier/{supplierId}")
    public List<PurchaseOrderDto> getOrdersBySupplierId(@PathVariable Long supplierId) {
        // Log the request to get orders by supplier ID
        log.info("Received request to get purchase orders by supplier ID: {}", supplierId);
        try {
            List<PurchaseOrderDto> orders = orderService.getOrdersBySupplier(supplierId);
            // Log the number of orders found for the supplier
            log.debug("Found {} purchase orders for supplier ID {}.", orders.size(), supplierId);
            return orders;
        } catch (Exception e) {
            // Log errors during retrieval
            log.error("Error fetching purchase orders for supplier ID {}: {}", supplierId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/product/{productId}")
    public List<PurchaseOrderDto> getOrdersByProductId(@PathVariable Long productId) {
        // Log the request to get orders by product ID
        log.info("Received request to get purchase orders by product ID: {}", productId);
        try {
            List<PurchaseOrderDto> orders = orderService.getOrdersByProduct(productId);
            // Log the number of orders found for the product
            log.debug("Found {} purchase orders for product ID {}.", orders.size(), productId);
            return orders;
        } catch (Exception e) {
            // Log errors during retrieval
            log.error("Error fetching purchase orders for product ID {}: {}", productId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/date-range")
    public List<PurchaseOrderDto> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        // Log the request to get orders within a date range
        log.info("Received request to get purchase orders between {} and {}.", startDate, endDate);
        try {
            List<PurchaseOrderDto> orders = orderService.getOrdersBetweenDates(startDate, endDate);
            // Log the number of orders found in the date range
            log.debug("Found {} purchase orders between {} and {}.", orders.size(), startDate, endDate);
            return orders;
        } catch (Exception e) {
            // Log errors during retrieval
            log.error("Error fetching purchase orders between {} and {}: {}", startDate, endDate, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        // Log the delete request
        log.info("Received request to delete purchase order with ID: {}", id);
        try {
            orderService.deleteOrder(id);
            // Log successful deletion
            log.debug("Purchase order with ID {} deleted successfully.", id);
        } catch (Exception e) {
            // Log errors during deletion
            log.error("Error deleting purchase order with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}