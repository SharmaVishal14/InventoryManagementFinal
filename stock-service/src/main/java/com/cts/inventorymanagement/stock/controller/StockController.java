package com.cts.inventorymanagement.stock.controller;

import com.cts.inventorymanagement.stock.dto.StockDto;
import com.cts.inventorymanagement.stock.dto.StockUpdateRequest;
import com.cts.inventorymanagement.stock.dto.ReorderLevelUpdateRequest;
import com.cts.inventorymanagement.stock.service.StockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private static final Logger log = LoggerFactory.getLogger(StockController.class);

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/{productId}")
    public StockDto getStock(@PathVariable Long productId) {
        log.info("Received request to get stock for product ID: {}", productId);
        try {
            StockDto stock = stockService.getStock(productId);
            if (stock != null) {
                log.debug("Successfully retrieved stock for product ID {}: Quantity = {}", productId, stock.getQuantity());
            } else {
                log.warn("Stock information for product ID {} not found.", productId);
            }
            return stock;
        } catch (Exception e) {
            log.error("Error fetching stock for product ID {}: {}", productId, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{productId}")
    public StockDto updateStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockUpdateRequest request) {
        log.info("Received request to update stock for product ID {}. Operation: {}, Amount: {}",
                productId, request.getOperation());
        try {
            StockDto updatedStock = stockService.updateStock(productId, request);
            log.debug("Stock for product ID {} updated successfully. New quantity: {}", productId, updatedStock.getQuantity());
            return updatedStock;
        } catch (Exception e) {
            log.error("Error updating stock for product ID {}. Operation: {}, Amount: {}. Error: {}",
                    productId, request.getOperation(), e.getMessage(), e);
            throw e;
        }
    }

    @PatchMapping("/{productId}/reorder-level")
    public StockDto updateReorderLevel(
            @PathVariable Long productId,
            @Valid @RequestBody ReorderLevelUpdateRequest request) {
        log.info("Received request to update reorder level for product ID {} to {}", productId, request.getReorderLevel());
        try {
            StockDto updatedStock = stockService.updateReorderLevel(productId, request.getReorderLevel());
            log.debug("Reorder level for product ID {} updated successfully to {}", productId, updatedStock.getReorderLevel());
            return updatedStock;
        } catch (Exception e) {
            log.error("Error updating reorder level for product ID {}. Error: {}", productId, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StockDto addStock(@Valid @RequestBody StockDto stockDto) {
        log.info("Received request to add initial stock for product ID: {} with quantity: {}",
                stockDto.getProductId(), stockDto.getQuantity());
        try {
            StockDto newStock = stockService.addStock(stockDto);
            log.debug("Initial stock added successfully for product ID {}. Current quantity: {}",
                    newStock.getProductId(), newStock.getQuantity());
            return newStock;
        } catch (Exception e) {
            log.error("Error adding initial stock for product ID {}: {}", stockDto.getProductId(), e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/low")
    public List<StockDto> getLowStockItems() {
        log.info("Received request to get all low stock items.");
        try {
            List<StockDto> lowStockItems = stockService.getLowStockItems();
            log.debug("Successfully retrieved {} low stock items.", lowStockItems.size());
            return lowStockItems;
        } catch (Exception e) {
            log.error("Error fetching low stock items: {}", e.getMessage(), e);
            throw e;
        }
    }
}