package com.stockmanagement.inventory.application.service;

import com.stockmanagement.inventory.application.dto.response.StockResponse;
import com.stockmanagement.inventory.application.mapper.StockMapper;
import com.stockmanagement.inventory.domain.exception.StockNotFoundException;
import com.stockmanagement.inventory.domain.model.Stock;
import com.stockmanagement.inventory.domain.model.valueobject.*;
import com.stockmanagement.inventory.domain.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * StockQueryService - Handles read-only stock queries.
 * 
 * CQRS Pattern: Query Service for READ operations only.
 * 
 * QUERIES:
 * - Get stock by ID
 * - Get stock by SKU and location
 * - Get all stock for a SKU (across locations)
 * - Get all stock at a location
 * 
 * READ-ONLY: No modifications, no events
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
@Service
@Transactional(readOnly = true)
public class StockQueryService {

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;

    public StockQueryService(
            StockRepository stockRepository,
            StockMapper stockMapper) {
        this.stockRepository = stockRepository;
        this.stockMapper = stockMapper;
    }

    /**
     * Gets stock by ID.
     * 
     * @param stockId Stock identifier
     * @return Stock response
     * @throws StockNotFoundException if not found
     */
    public StockResponse getById(String stockId) {
        StockId id = StockId.of(stockId);
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new StockNotFoundException(
                        "Stock not found: " + stockId));
        return stockMapper.toResponse(stock);
    }

    /**
     * Gets stock by SKU and location.
     * 
     * @param sku        Product SKU
     * @param locationId Storage location
     * @return Stock response
     * @throws StockNotFoundException if not found
     */
    public StockResponse getBySkuAndLocation(String sku, String locationId) {
        ProductSKU productSku = ProductSKU.of(sku);
        LocationId location = LocationId.of(locationId);

        Stock stock = stockRepository.findBySkuAndLocation(productSku, location)
                .orElseThrow(() -> new StockNotFoundException(
                        String.format("Stock not found for SKU: %s at location: %s",
                                sku, locationId)));
        return stockMapper.toResponse(stock);
    }

    /**
     * Gets all stock for a SKU across all locations.
     * 
     * @param sku Product SKU
     * @return List of stock responses
     */
    public List<StockResponse> getBySku(String sku) {
        ProductSKU productSku = ProductSKU.of(sku);
        List<Stock> stocks = stockRepository.findBySku(productSku);
        return stockMapper.toResponseList(stocks);
    }

    /**
     * Gets all stock at a location.
     * 
     * @param locationId Storage location
     * @return List of stock responses
     */
    public List<StockResponse> getByLocation(String locationId) {
        LocationId location = LocationId.of(locationId);
        List<Stock> stocks = stockRepository.findByLocation(location);
        return stockMapper.toResponseList(stocks);
    }

    /**
     * Gets all stock in the system.
     * 
     * @return List of all stock responses
     */
    public List<StockResponse> getAll() {
        List<Stock> stocks = stockRepository.findAll();
        return stockMapper.toResponseList(stocks);
    }

    /**
     * Gets all stock in the system with pagination.
     * 
     * BEST PRACTICE: Always paginate large result sets
     * DEFAULT: page=0, size=20 if not specified
     * 
     * @param pageable Pagination parameters
     * @return Paginated stock responses
     */
    public com.stockmanagement.inventory.application.dto.response.PagedStockResponse getAllPaged(
            org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.domain.Page<Stock> stockPage = stockRepository.findAll(pageable);
        List<StockResponse> content = stockMapper.toResponseList(stockPage.getContent());
        return com.stockmanagement.inventory.application.dto.response.PagedStockResponse.from(stockPage, content);
    }
}
