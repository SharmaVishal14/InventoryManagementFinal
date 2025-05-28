package com.cts.inventorymanagement.order.service;

import java.util.List;
import java.util.Map;

import com.cts.inventorymanagement.order.dto.OrderInputDto;
import com.cts.inventorymanagement.order.dto.OrderOutputDto;
import com.cts.inventorymanagement.order.model.Order;

public interface OrderService {
    List<OrderOutputDto> getAllOrders();
    List<OrderOutputDto> getOrdersOfProduct(Long productId);
    List<OrderOutputDto> getOrdersOfCustomer(Long customerId);
    OrderOutputDto createOrder(OrderInputDto orderDto);
    Map<String, String> updateOrderStatus(Long orderId, Order.Status orderStatus);
}