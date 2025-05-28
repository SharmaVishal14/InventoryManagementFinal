package com.cts.inventorymanagement.stock.service;

import java.util.List;

import com.cts.inventorymanagement.stock.dto.StockDto;
import com.cts.inventorymanagement.stock.dto.StockUpdateRequest;

public interface StockService {
    StockDto getStock(Long productId);
    StockDto updateStock(Long productId, StockUpdateRequest request);
    StockDto addStock(StockDto stockDto);
    List<StockDto> getLowStockItems();
    StockDto updateReorderLevel(Long productId, Integer reorderLevel);
}