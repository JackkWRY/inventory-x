package com.stockmanagement.inventory.presentation.rest;

import com.stockmanagement.inventory.application.dto.command.*;
import com.stockmanagement.inventory.application.dto.response.StockResponse;
import com.stockmanagement.inventory.application.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * StockController - REST API for stock management operations.
 * 
 * BASE PATH: /api/v1/stocks
 * 
 * ENDPOINTS:
 * - POST /receive - Receive stock into warehouse
 * - POST /reserve - Reserve stock for order
 * - POST /release - Release reservation
 * - POST /confirm - Confirm reservation (sale)
 * - POST /adjust - Adjust stock quantity
 * - GET /{id} - Get stock by ID
 * - GET ?sku=X - Query stocks by SKU
 * - GET ?locationId=X - Query stocks by location
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/stocks")
public class StockController {

    private final ReceiveStockUseCase receiveStockUseCase;
    private final ReserveStockUseCase reserveStockUseCase;
    private final ReleaseReservationUseCase releaseReservationUseCase;
    private final ConfirmReservationUseCase confirmReservationUseCase;
    private final AdjustStockUseCase adjustStockUseCase;
    private final QueryStockUseCase queryStockUseCase;

    public StockController(
            ReceiveStockUseCase receiveStockUseCase,
            ReserveStockUseCase reserveStockUseCase,
            ReleaseReservationUseCase releaseReservationUseCase,
            ConfirmReservationUseCase confirmReservationUseCase,
            AdjustStockUseCase adjustStockUseCase,
            QueryStockUseCase queryStockUseCase) {
        this.receiveStockUseCase = receiveStockUseCase;
        this.reserveStockUseCase = reserveStockUseCase;
        this.releaseReservationUseCase = releaseReservationUseCase;
        this.confirmReservationUseCase = confirmReservationUseCase;
        this.adjustStockUseCase = adjustStockUseCase;
        this.queryStockUseCase = queryStockUseCase;
    }

    /**
     * Receive stock into warehouse.
     * 
     * POST /api/v1/stocks/receive
     * 
     * USE CASE: Supplier delivery, stock transfer receipt
     * 
     * @param command Receive stock command
     * @return Stock response with updated quantities
     */
    @PostMapping("/receive")
    public ResponseEntity<StockResponse> receiveStock(
            @RequestBody ReceiveStockCommand command) {
        StockResponse response = receiveStockUseCase.execute(command);
        return ResponseEntity.ok(response);
    }

    /**
     * Reserve stock for order.
     * 
     * POST /api/v1/stocks/reserve
     * 
     * USE CASE: Customer places order
     * 
     * @param command Reserve stock command
     * @return Stock response with updated quantities
     */
    @PostMapping("/reserve")
    public ResponseEntity<StockResponse> reserveStock(
            @RequestBody ReserveStockCommand command) {
        StockResponse response = reserveStockUseCase.execute(command);
        return ResponseEntity.ok(response);
    }

    /**
     * Release reservation (cancel).
     * 
     * POST /api/v1/stocks/release
     * 
     * USE CASE: Order cancelled, payment failed
     * 
     * @param command Release reservation command
     * @return Stock response with updated quantities
     */
    @PostMapping("/release")
    public ResponseEntity<StockResponse> releaseReservation(
            @RequestBody ReleaseReservationCommand command) {
        StockResponse response = releaseReservationUseCase.execute(command);
        return ResponseEntity.ok(response);
    }

    /**
     * Confirm reservation (complete sale).
     * 
     * POST /api/v1/stocks/confirm
     * 
     * USE CASE: Payment received, order confirmed
     * 
     * @param command Confirm reservation command
     * @return Stock response with updated quantities
     */
    @PostMapping("/confirm")
    public ResponseEntity<StockResponse> confirmReservation(
            @RequestBody ConfirmReservationCommand command) {
        StockResponse response = confirmReservationUseCase.execute(command);
        return ResponseEntity.ok(response);
    }

    /**
     * Adjust stock quantity manually.
     * 
     * POST /api/v1/stocks/adjust
     * 
     * USE CASE: Physical inventory count, damaged goods
     * 
     * @param command Adjust stock command
     * @return Stock response with updated quantities
     */
    @PostMapping("/adjust")
    public ResponseEntity<StockResponse> adjustStock(
            @RequestBody AdjustStockCommand command) {
        StockResponse response = adjustStockUseCase.execute(command);
        return ResponseEntity.ok(response);
    }

    /**
     * Get stock by ID.
     * 
     * GET /api/v1/stocks/{id}
     * 
     * @param id Stock identifier
     * @return Stock response
     */
    @GetMapping("/{id}")
    public ResponseEntity<StockResponse> getStockById(@PathVariable String id) {
        StockResponse response = queryStockUseCase.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Query stocks by SKU or location.
     * 
     * GET /api/v1/stocks?sku=PROD-001
     * GET /api/v1/stocks?locationId=WH-01
     * GET /api/v1/stocks?sku=PROD-001&locationId=WH-01
     * 
     * @param sku        Product SKU (optional)
     * @param locationId Storage location (optional)
     * @return List of stock responses
     */
    @GetMapping
    public ResponseEntity<List<StockResponse>> queryStocks(
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) String locationId) {

        if (sku != null && locationId != null) {
            // Both parameters: get specific stock
            StockResponse response = queryStockUseCase.getBySkuAndLocation(sku, locationId);
            return ResponseEntity.ok(List.of(response));
        } else if (sku != null) {
            // SKU only: get all locations
            List<StockResponse> responses = queryStockUseCase.getBySku(sku);
            return ResponseEntity.ok(responses);
        } else if (locationId != null) {
            // Location only: get all products
            List<StockResponse> responses = queryStockUseCase.getByLocation(locationId);
            return ResponseEntity.ok(responses);
        } else {
            // No parameters: return all stocks
            List<StockResponse> responses = queryStockUseCase.getAll();
            return ResponseEntity.ok(responses);
        }
    }

    /**
     * Query stocks with pagination.
     * 
     * GET /api/v1/stocks/paged
     * GET /api/v1/stocks/paged?page=0&size=20
     * GET /api/v1/stocks/paged?page=0&size=10&sort=sku,asc
     * 
     * BEST PRACTICE: Always paginate large result sets
     * DEFAULT: page=0, size=20
     * 
     * @param page Page number (0-indexed), default 0
     * @param size Number of items per page, default 20, max 100
     * @return Paginated stock response
     */
    @GetMapping("/paged")
    public ResponseEntity<com.stockmanagement.inventory.application.dto.response.PagedStockResponse> queryStocksPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        // Enforce max page size to prevent memory issues
        int safeSize = Math.min(size, 100);

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page,
                safeSize);

        com.stockmanagement.inventory.application.dto.response.PagedStockResponse response = queryStockUseCase
                .getAllPaged(pageable);

        return ResponseEntity.ok(response);
    }
}
