package com.cts.inventorymanagement.order.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cts.inventorymanagement.order.client.ProductClient;
import com.cts.inventorymanagement.order.client.StockClient;
import com.cts.inventorymanagement.order.dto.OrderInputDto;
import com.cts.inventorymanagement.order.dto.OrderItemInputDto;
import com.cts.inventorymanagement.order.dto.OrderItemOutputDto;
import com.cts.inventorymanagement.order.dto.OrderOutputDto;
import com.cts.inventorymanagement.order.dto.ProductDto;
import com.cts.inventorymanagement.order.dto.StockDto;
import com.cts.inventorymanagement.order.dto.StockUpdateRequest;
import com.cts.inventorymanagement.order.dto.StockUpdateRequest.Operation;
import com.cts.inventorymanagement.order.exceptions.InsufficientStockException;
import com.cts.inventorymanagement.order.exceptions.ProductNotFoundException;
import com.cts.inventorymanagement.order.model.Order;
import com.cts.inventorymanagement.order.model.Order.Status;
import com.cts.inventorymanagement.order.model.OrderItem;
import com.cts.inventorymanagement.order.repository.OrderRepository;
import com.cts.inventorymanagement.order.service.OrderService;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepo;
    private final ProductClient productClient;
    private final StockClient stockClient;

    @Override
    @Transactional
    public OrderOutputDto createOrder(OrderInputDto orderInput) {
        validateOrderInput(orderInput);
        Map<Long, Integer> productQuantities = groupItemsByProduct(orderInput);
        verifyStockAvailability(productQuantities);
        
        Order order = createOrderEntity(orderInput);
        processStockUpdates(order);
        Order savedOrder = orderRepo.save(order);
        
        return convertToOutputDto(savedOrder);
    }

    private void validateOrderInput(OrderInputDto orderInput) {
        if (orderInput.getItems() == null || orderInput.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
    }

    private Map<Long, Integer> groupItemsByProduct(OrderInputDto orderInput) {
        return orderInput.getItems().stream()
            .collect(Collectors.groupingBy(
                OrderItemInputDto::getProductId,
                Collectors.summingInt(OrderItemInputDto::getQuantity)
            ));
    }

    private void verifyStockAvailability(Map<Long, Integer> productQuantities) {
        productQuantities.forEach((productId, totalQty) -> {
            try {
                StockDto stock = stockClient.getStock(productId);
                if (stock.getQuantity() < totalQty) {
                    throw new InsufficientStockException(
                        "Insufficient stock for product: " + productId +
                        ". Available: " + stock.getQuantity() +
                        ", Requested: " + totalQty
                    );
                }
            } catch (FeignException e) {
                throw new ProductNotFoundException("Product not found with id: " + productId);
            }
        });
    }

    private Order createOrderEntity(OrderInputDto orderInput) {
        Order order = new Order();
        order.setCustomerId(orderInput.getCustomerId());
        order.setOrderStatus(Order.Status.PENDING);
        order.setOrderdate(LocalDateTime.now());

        List<OrderItem> items = orderInput.getItems().stream()
            .map(itemInput -> {
                OrderItem item = convertToOrderItem(itemInput);
                item.setOrder(order); // <<< THIS IS CRUCIAL
                return item;
            })
            .collect(Collectors.toList());
        
        order.setItems(items);
        return order;
    }

    private OrderItem convertToOrderItem(OrderItemInputDto itemInput) {
        OrderItem item = new OrderItem();
        item.setProductId(itemInput.getProductId());
        item.setQuantity(itemInput.getQuantity());
        return item;
    }

    private void processStockUpdates(Order order) {
        order.getItems().forEach(item -> {
            StockUpdateRequest request = new StockUpdateRequest(
                item.getQuantity(),
                Operation.DECREMENT
            );
            stockClient.updateStock(item.getProductId(), request);
        });
    }

    @Override
    public List<OrderOutputDto> getAllOrders() {
        return convertOrdersToDtos(orderRepo.findAll());
    }

    @Override
    public List<OrderOutputDto> getOrdersOfCustomer(Long customerId) {
        return convertOrdersToDtos(orderRepo.findByCustomerId(customerId));
    }

    @Override
    public List<OrderOutputDto> getOrdersOfProduct(Long productId) {
        return convertOrdersToDtos(orderRepo.findByProductId(productId));
    }

    private List<OrderOutputDto> convertOrdersToDtos(List<Order> orders) {
        return orders.stream()
            .map(this::convertToOutputDto)
            .collect(Collectors.toList());
    }

    private OrderOutputDto convertToOutputDto(Order order) {
        return OrderOutputDto.builder()
            .id(order.getId())
            .customerId(order.getCustomerId())
            .orderdate(order.getOrderdate())
            .orderStatus(order.getOrderStatus())
            .items(convertItemsToOutputDtos(order.getItems()))
            .totalPrice(calculateTotalPrice(order.getItems()))
            .build();
    }

    private List<OrderItemOutputDto> convertItemsToOutputDtos(List<OrderItem> items) {
        return items.stream()
            .map(this::convertToItemOutputDto)
            .collect(Collectors.toList());
    }

    private OrderItemOutputDto convertToItemOutputDto(OrderItem item) {
        ProductDto product = productClient.getProductById(item.getProductId());
        BigDecimal unitPrice = product.getPrice();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

        return OrderItemOutputDto.builder()
            .id(item.getId())
            .productId(item.getProductId())
            .quantity(item.getQuantity())
            .unitPrice(unitPrice)
            .totalPrice(totalPrice)
            .build();
    }

    private BigDecimal calculateTotalPrice(List<OrderItem> items) {
        return items.stream()
            .map(item -> {
                ProductDto product = productClient.getProductById(item.getProductId());
                return product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<String, String> updateOrderStatus(Long orderId, Order.Status newStatus) {
        Order order = orderRepo.findById(orderId)
            .orElseThrow(() -> new ProductNotFoundException("Order not found with id: " + orderId));
        
        Order.Status currentStatus = order.getOrderStatus();
        
        // 1. Check if order is already cancelled
        if (currentStatus == Order.Status.CANCELLED) {
            throw new IllegalArgumentException("Cancelled orders cannot be modified");
        }
        
        // 2. Check chronological order
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new IllegalArgumentException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
        
        order.setOrderStatus(newStatus);
        orderRepo.save(order);
        
        // 3. If cancelling, restore stock
        if (newStatus == Order.Status.CANCELLED) {
            restoreStock(order);
        }
        
        return Map.of(
            "status", newStatus.toString(),
            "message", "Order status updated successfully"
        );
    }

    private boolean isValidTransition(Order.Status current, Order.Status newStatus) {
        if (current == newStatus) return true;
        
        return switch (current) {
            case PENDING -> 
                newStatus == Order.Status.SHIPPED || 
                newStatus == Order.Status.DELIVERED || 
                newStatus == Order.Status.CANCELLED;
            case SHIPPED -> 
                newStatus == Order.Status.DELIVERED || 
                newStatus == Order.Status.CANCELLED;
            case DELIVERED -> false;
            case CANCELLED -> false;
        };
    }

    private void restoreStock(Order order) {
        order.getItems().forEach(item -> {
            StockUpdateRequest request = new StockUpdateRequest(
                item.getQuantity(),
                StockUpdateRequest.Operation.INCREMENT
            );
            try {
                stockClient.updateStock(item.getProductId(), request);
            } catch (Exception e) {
                logger.error("Failed to restore stock for product {}: {}", 
                    item.getProductId(), e.getMessage());
            }
        });
    }
}