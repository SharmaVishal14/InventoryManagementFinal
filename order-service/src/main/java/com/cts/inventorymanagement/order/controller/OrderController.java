package com.cts.inventorymanagement.order.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cts.inventorymanagement.order.dto.OrderInputDto;
import com.cts.inventorymanagement.order.dto.OrderOutputDto;
import com.cts.inventorymanagement.order.model.Order;
import com.cts.inventorymanagement.order.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<OrderOutputDto>> getAllOrders(@RequestParam(required = false) Long customerId) {
        logger.info("GET /api/orders - customerId: {}", customerId);
        List<OrderOutputDto> orders = new ArrayList<>();
        if (customerId == null) {
            logger.debug("Fetching all orders.");
            orders = service.getAllOrders();
        } else {
            logger.debug("Fetching orders for customerId: {}", customerId);
            orders = service.getOrdersOfCustomer(customerId);
        }
        logger.info("Returning {} orders.", orders.size());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<OrderOutputDto>> getOrdersOfProduct(@PathVariable Long productId) {
        logger.info("GET /api/orders/{}", productId);
        logger.debug("Fetching orders for productId: {}", productId);
        List<OrderOutputDto> orders = service.getOrdersOfProduct(productId);
        logger.info("Returning {} orders.", orders.size());
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<OrderOutputDto> createOrder(@Valid @RequestBody OrderInputDto orderDto) {
        logger.info("POST /api/orders - body: {}", orderDto);
        OrderOutputDto createdOrder = service.createOrder(orderDto);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<Map<String, String>> updateOrderStatus(
        @PathVariable Long orderId, 
        @RequestParam Order.Status orderStatus
    ) {
        Map<String, String> result = service.updateOrderStatus(orderId, orderStatus);
        return ResponseEntity.ok(result);
    }
}