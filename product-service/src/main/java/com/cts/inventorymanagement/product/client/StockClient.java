package com.cts.inventorymanagement.product.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cts.inventorymanagement.product.dto.StockDto;
import com.cts.inventorymanagement.product.dto.StockUpdateRequest;

import jakarta.validation.Valid;

@FeignClient(name="stock-service")
public interface StockClient {
	  @PostMapping("/stocks")
	  StockDto addStock(@RequestBody StockDto stockDto);
	
	@GetMapping("/stocks/{productId}")
    public StockDto getStock(@PathVariable Long productId);
	
	 @GetMapping("/stocks/low")
	 public List<StockDto> getLowStockItems();
	 
	 @PutMapping("/stocks/{productId}")
	    public StockDto updateStock(
        @PathVariable Long productId,
        @Valid @RequestBody StockUpdateRequest request);
	
}
