package com.stockmanagement.inventory.application.usecase;

import com.stockmanagement.inventory.application.dto.command.ReceiveStockCommand;
import com.stockmanagement.inventory.application.dto.response.StockResponse;
import com.stockmanagement.inventory.application.event.DomainEventPublisher;
import com.stockmanagement.inventory.application.mapper.StockMapper;
import com.stockmanagement.inventory.domain.model.*;
import com.stockmanagement.inventory.domain.model.valueobject.*;
import com.stockmanagement.inventory.domain.repository.StockRepository;
import com.stockmanagement.inventory.application.service.ReceiveStockUseCase;
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
class ReceiveStockUseCaseTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProductRepository productRepository; // needed to be mocked

    @Mock
    private StockMapper stockMapper;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private ReceiveStockUseCase receiveStockUseCase;

    private Stock stock;

    @BeforeEach
    void setUp() {
        stock = Stock.create(
                "P001",
                new ProductSKU("SKU-001"),
                new LocationId("L001"),
                UnitOfMeasure.PIECE);
    }

    @Test
    void shouldIncreaseStockWhenStockExists() {
        // Arrange
        // Command (String sku, String locationId, String quantity, String uom, String
        // reason, String performedBy)
        ReceiveStockCommand command = new ReceiveStockCommand(
                "SKU-001",
                "L001",
                "5",
                "PIECE",
                "Remark",
                "Admin");

        when(stockRepository.findBySkuAndLocation(any(ProductSKU.class), any(LocationId.class)))
                .thenReturn(Optional.of(stock));

        when(stockRepository.save(any(Stock.class))).thenReturn(stock);
        when(stockMapper.toResponse(any(Stock.class))).thenReturn(new StockResponse(
                "id", "sku", "loc", "10", "0", "PIECE", 1L, "now", "now")); // fine, just non-null

        // Act
        receiveStockUseCase.execute(command);

        // Assert
        verify(stockRepository).save(stock);
        verify(eventPublisher).publish(any());
    }
}
