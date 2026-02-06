package com.stockmanagement.inventory.application.usecase;

import com.stockmanagement.inventory.application.dto.command.ReserveStockCommand;
import com.stockmanagement.inventory.application.dto.response.StockResponse;
import com.stockmanagement.inventory.application.event.DomainEventPublisher;
import com.stockmanagement.inventory.application.mapper.StockMapper;
import com.stockmanagement.inventory.domain.model.Stock;
import com.stockmanagement.inventory.domain.model.valueobject.*;
import com.stockmanagement.inventory.domain.repository.StockRepository;
import com.stockmanagement.inventory.domain.service.ReservationPolicy;
import com.stockmanagement.inventory.application.service.ReserveStockUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReserveStockUseCaseTest {

        @Mock
        private StockRepository stockRepository;

        @Mock
        private ReservationPolicy reservationPolicy;

        @Mock
        private StockMapper stockMapper;

        @Mock
        private DomainEventPublisher eventPublisher;

        @InjectMocks
        private ReserveStockUseCase reserveStockUseCase;

        private Stock stock;

        @BeforeEach
        void setUp() {
                stock = Stock.create(
                                "P001",
                                new ProductSKU("SKU-001"),
                                new LocationId("L001"),
                                UnitOfMeasure.PIECE);
                // Add initial stock
                stock.receiveStock(Quantity.of(100), "Initial", "Admin");
        }

        @Test
        void shouldReserveStockSuccessfully() {
                // Arrange
                ReserveStockCommand command = new ReserveStockCommand(
                                "SKU-001", "L001", "10", "ORDER-123");

                when(stockRepository.findBySkuAndLocation(any(ProductSKU.class), any(LocationId.class)))
                                .thenReturn(Optional.of(stock));
                when(reservationPolicy.canReserve(any(Stock.class), any(Quantity.class)))
                                .thenReturn(true);
                when(stockRepository.save(any(Stock.class))).thenReturn(stock);
                when(stockMapper.toResponse(any(Stock.class))).thenReturn(new StockResponse(
                                "id", "SKU-001", "L001", "90", "10", "PIECE", 1L, "now", "now"));

                // Act
                reserveStockUseCase.execute(command);

                // Assert
                verify(stockRepository).save(stock);
                verify(eventPublisher).publish(any());
        }
}
