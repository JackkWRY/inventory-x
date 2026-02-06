package com.stockmanagement.inventory.application.usecase;

import com.stockmanagement.inventory.application.dto.command.ReleaseReservationCommand;
import com.stockmanagement.inventory.application.dto.response.StockResponse;
import com.stockmanagement.inventory.application.event.DomainEventPublisher;
import com.stockmanagement.inventory.application.mapper.StockMapper;
import com.stockmanagement.inventory.domain.model.Stock;
import com.stockmanagement.inventory.domain.model.valueobject.*;
import com.stockmanagement.inventory.domain.repository.StockRepository;
import com.stockmanagement.inventory.application.service.ReleaseReservationUseCase;
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
class ReleaseReservationUseCaseTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockMapper stockMapper;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private ReleaseReservationUseCase releaseReservationUseCase;

    private Stock stock;

    @BeforeEach
    void setUp() {
        stock = Stock.create(
                "P001",
                new ProductSKU("SKU-001"),
                new LocationId("L001"),
                UnitOfMeasure.PIECE);
        stock.receiveStock(Quantity.of(100), "Initial", "Admin");
        stock.reserve(Quantity.of(20), "ORDER-123");
    }

    @Test
    void shouldReleaseReservationSuccessfully() {
        // Arrange
        ReleaseReservationCommand command = new ReleaseReservationCommand(
                stock.getId().toString(), "20", "ORDER-123");

        when(stockRepository.findById(any(StockId.class)))
                .thenReturn(Optional.of(stock));
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);
        when(stockMapper.toResponse(any(Stock.class))).thenReturn(new StockResponse(
                "id", "SKU-001", "L001", "100", "0", "PIECE", 1L, "now", "now"));

        // Act
        releaseReservationUseCase.execute(command);

        // Assert
        verify(stockRepository).save(stock);
        verify(eventPublisher).publish(any());
    }
}
