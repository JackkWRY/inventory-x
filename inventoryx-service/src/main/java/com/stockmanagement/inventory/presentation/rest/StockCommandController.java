package com.stockmanagement.inventory.presentation.rest;

import com.stockmanagement.inventory.application.dto.command.*;
import com.stockmanagement.inventory.application.dto.response.StockResponse;
import com.stockmanagement.inventory.application.service.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * StockCommandController - REST API for stock write operations (CQRS Command
 * side).
 * 
 * BASE PATH: /api/v1/stocks
 * 
 * ENDPOINTS (POST - Write operations):
 * - POST /receive - Receive stock into warehouse
 * - POST /reserve - Reserve stock for order
 * - POST /release - Release reservation
 * - POST /confirm - Confirm reservation (sale)
 * - POST /adjust - Adjust stock quantity
 * - POST /withdraw - Withdraw stock for internal use
 * - POST /sale - Quick sale (POS)
 * 
 * @author InventoryX Development Team
 * @since 2026-01-17
 */
@RestController
@RequestMapping("/stocks")
public class StockCommandController {

    private final ReceiveStockUseCase receiveStockUseCase;
    private final ReserveStockUseCase reserveStockUseCase;
    private final ReleaseReservationUseCase releaseReservationUseCase;
    private final ConfirmReservationUseCase confirmReservationUseCase;
    private final AdjustStockUseCase adjustStockUseCase;
    private final WithdrawStockUseCase withdrawStockUseCase;
    private final QuickSaleUseCase quickSaleUseCase;

    public StockCommandController(
            ReceiveStockUseCase receiveStockUseCase,
            ReserveStockUseCase reserveStockUseCase,
            ReleaseReservationUseCase releaseReservationUseCase,
            ConfirmReservationUseCase confirmReservationUseCase,
            AdjustStockUseCase adjustStockUseCase,
            WithdrawStockUseCase withdrawStockUseCase,
            QuickSaleUseCase quickSaleUseCase) {
        this.receiveStockUseCase = receiveStockUseCase;
        this.reserveStockUseCase = reserveStockUseCase;
        this.releaseReservationUseCase = releaseReservationUseCase;
        this.confirmReservationUseCase = confirmReservationUseCase;
        this.adjustStockUseCase = adjustStockUseCase;
        this.withdrawStockUseCase = withdrawStockUseCase;
        this.quickSaleUseCase = quickSaleUseCase;
    }

    /**
     * Receive stock into warehouse.
     * 
     * POST /api/v1/stocks/receive
     * USE CASE: Supplier delivery, stock transfer receipt
     */
    @PostMapping("/receive")
    public ResponseEntity<StockResponse> receiveStock(
            @Valid @RequestBody ReceiveStockCommand command) {
        return ResponseEntity.ok(receiveStockUseCase.execute(command));
    }

    /**
     * Reserve stock for order.
     * 
     * POST /api/v1/stocks/reserve
     * USE CASE: Customer places order
     */
    @PostMapping("/reserve")
    public ResponseEntity<StockResponse> reserveStock(
            @Valid @RequestBody ReserveStockCommand command) {
        return ResponseEntity.ok(reserveStockUseCase.execute(command));
    }

    /**
     * Release reservation (cancel).
     * 
     * POST /api/v1/stocks/release
     * USE CASE: Order cancelled, payment failed
     */
    @PostMapping("/release")
    public ResponseEntity<StockResponse> releaseReservation(
            @Valid @RequestBody ReleaseReservationCommand command) {
        return ResponseEntity.ok(releaseReservationUseCase.execute(command));
    }

    /**
     * Confirm reservation (complete sale).
     * 
     * POST /api/v1/stocks/confirm
     * USE CASE: Payment received, order confirmed
     */
    @PostMapping("/confirm")
    public ResponseEntity<StockResponse> confirmReservation(
            @Valid @RequestBody ConfirmReservationCommand command) {
        return ResponseEntity.ok(confirmReservationUseCase.execute(command));
    }

    /**
     * Adjust stock quantity manually.
     * 
     * POST /api/v1/stocks/adjust
     * USE CASE: Physical inventory count, damaged goods
     */
    @PostMapping("/adjust")
    public ResponseEntity<StockResponse> adjustStock(
            @Valid @RequestBody AdjustStockCommand command) {
        return ResponseEntity.ok(adjustStockUseCase.execute(command));
    }

    /**
     * Withdraw stock for internal use.
     * 
     * POST /api/v1/stocks/withdraw
     * USE CASE: Department requisition, material consumption
     */
    @PostMapping("/withdraw")
    public ResponseEntity<StockResponse> withdrawStock(
            @Valid @RequestBody WithdrawStockCommand command) {
        return ResponseEntity.ok(withdrawStockUseCase.execute(command));
    }

    /**
     * Quick sale for POS/Walk-in.
     * 
     * POST /api/v1/stocks/sale
     * USE CASE: Point of Sale, retail counter, immediate sales
     */
    @PostMapping("/sale")
    public ResponseEntity<StockResponse> quickSale(
            @Valid @RequestBody QuickSaleCommand command) {
        return ResponseEntity.ok(quickSaleUseCase.execute(command));
    }
}
