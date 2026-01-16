package com.stockmanagement.inventory.presentation.rest;

import com.stockmanagement.inventory.application.dto.response.PagedStockResponse;
import com.stockmanagement.inventory.application.dto.response.StockMovementResponse;
import com.stockmanagement.inventory.application.dto.response.StockResponse;
import com.stockmanagement.inventory.application.service.StockMovementQueryService;
import com.stockmanagement.inventory.application.service.StockQueryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * StockQueryController - REST API for stock read operations (CQRS Query side).
 * 
 * BASE PATH: /api/v1/stocks
 * 
 * ENDPOINTS (GET - Read operations):
 * - GET /{id} - Get stock by ID
 * - GET /{id}/movements - Get stock movement history
 * - GET ?sku=X - Query stocks by SKU
 * - GET ?locationId=X - Query stocks by location
 * - GET /paged - Paginated stock list
 * 
 * @author InventoryX Development Team
 * @since 2026-01-17
 */
@RestController
@RequestMapping("/stocks")
public class StockQueryController {

    private final StockQueryService stockQueryService;
    private final StockMovementQueryService stockMovementQueryService;

    public StockQueryController(
            StockQueryService stockQueryService,
            StockMovementQueryService stockMovementQueryService) {
        this.stockQueryService = stockQueryService;
        this.stockMovementQueryService = stockMovementQueryService;
    }

    /**
     * Get stock by ID.
     * 
     * GET /api/v1/stocks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<StockResponse> getStockById(@PathVariable String id) {
        return ResponseEntity.ok(stockQueryService.getById(id));
    }

    /**
     * Get stock movement history.
     * 
     * GET /api/v1/stocks/{id}/movements
     * USE CASE: View movement history on Stock Detail page
     */
    @GetMapping("/{id}/movements")
    public ResponseEntity<List<StockMovementResponse>> getStockMovements(@PathVariable String id) {
        return ResponseEntity.ok(stockMovementQueryService.getMovementsByStockId(id));
    }

    /**
     * Query stocks by SKU or location.
     * 
     * GET /api/v1/stocks?sku=PROD-001
     * GET /api/v1/stocks?locationId=WH-01
     * GET /api/v1/stocks?sku=PROD-001&locationId=WH-01
     */
    @GetMapping
    public ResponseEntity<List<StockResponse>> queryStocks(
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) String locationId) {

        if (sku != null && locationId != null) {
            StockResponse response = stockQueryService.getBySkuAndLocation(sku, locationId);
            return ResponseEntity.ok(List.of(response));
        } else if (sku != null) {
            return ResponseEntity.ok(stockQueryService.getBySku(sku));
        } else if (locationId != null) {
            return ResponseEntity.ok(stockQueryService.getByLocation(locationId));
        } else {
            return ResponseEntity.ok(stockQueryService.getAll());
        }
    }

    /**
     * Query stocks with pagination.
     * 
     * GET /api/v1/stocks/paged
     * GET /api/v1/stocks/paged?page=0&size=20
     * 
     * DEFAULT: page=0, size=20, max=100
     */
    @GetMapping("/paged")
    public ResponseEntity<PagedStockResponse> queryStocksPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        int safeSize = Math.min(size, 100);
        Pageable pageable = PageRequest.of(page, safeSize);

        return ResponseEntity.ok(stockQueryService.getAllPaged(pageable));
    }
}
