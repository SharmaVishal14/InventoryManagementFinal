package com.cts.inventorymanagement.stock.service.impl;

import com.cts.inventorymanagement.stock.client.ProductClient;
import com.cts.inventorymanagement.stock.dto.ProductDto.ProductStatus;
import com.cts.inventorymanagement.stock.dto.StockDto;
import com.cts.inventorymanagement.stock.dto.StockUpdateRequest;
import com.cts.inventorymanagement.stock.exceptions.InsufficientStockException;
import com.cts.inventorymanagement.stock.exceptions.StockNotFoundException;
import com.cts.inventorymanagement.stock.model.Stock;
import com.cts.inventorymanagement.stock.repository.StockRepository;
import com.cts.inventorymanagement.stock.service.StockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private static final Logger log = LoggerFactory.getLogger(StockServiceImpl.class);
    private final StockRepository stockRepository;
    private final ProductClient productClient;

    @Override
    @Transactional
    public StockDto updateStock(Long productId, StockUpdateRequest request) {
        Stock stock = stockRepository.findById(productId).orElseThrow(() -> new StockNotFoundException("Stock not found"));
        int previousQuantity = stock.getQuantity();

        if (request.getOperation() == StockUpdateRequest.Operation.INCREMENT) {
            stock.setQuantity(stock.getQuantity() + request.getQuantity());
        } else {
            int newQuantity = stock.getQuantity() - request.getQuantity();
            if (newQuantity < 0) throw new InsufficientStockException("Insufficient stock");
            stock.setQuantity(newQuantity);
        }

        Stock updatedStock = stockRepository.save(stock);

        CompletableFuture.runAsync(() -> {
            try {
                if (updatedStock.getQuantity() == 0 && previousQuantity > 0) {
                    productClient.updateProductStatus(productId, ProductStatus.OUT_OF_STOCK);
                } else if (previousQuantity == 0 && updatedStock.getQuantity() > 0) {
                    productClient.updateProductStatus(productId, ProductStatus.ACTIVE);
                }
            } catch (Exception e) {
                log.error("Failed product status update: {}", e.getMessage());
            }
        });

        return convertToDto(updatedStock);
    }

    @Override
    public StockDto getStock(Long productId) {
        return convertToDto(stockRepository.findById(productId).orElseThrow(() -> new StockNotFoundException("Not found")));
    }

    @Override
    public StockDto addStock(StockDto stockDto) {
        if (stockRepository.existsById(stockDto.getProductId())) throw new IllegalArgumentException("Exists");
        Stock stock = new Stock();
        stock.setProductId(stockDto.getProductId());
        stock.setQuantity(stockDto.getQuantity());
        stock.setReorderLevel(stockDto.getReorderLevel());
        return convertToDto(stockRepository.save(stock));
    }

    @Override
    public List<StockDto> getLowStockItems() {
        return stockRepository.findAll().stream()
            .filter(s -> s.getQuantity() < s.getReorderLevel())
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StockDto updateReorderLevel(Long productId, Integer reorderLevel) {
        Stock stock = stockRepository.findById(productId).orElseThrow(() -> new StockNotFoundException("Stock not found for product ID: " + productId));
        stock.setReorderLevel(reorderLevel);
        Stock updatedStock = stockRepository.save(stock);
        return convertToDto(updatedStock);
    }

    private StockDto convertToDto(Stock stock) {
        return StockDto.builder()
            .productId(stock.getProductId())
            .quantity(stock.getQuantity())
            .reorderLevel(stock.getReorderLevel())
            .lowStock(stock.getQuantity() < stock.getReorderLevel())
            .build();
    }
}