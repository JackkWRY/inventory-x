package com.stockmanagement.inventory.application.usecase;

import com.stockmanagement.inventory.application.dto.command.WithdrawStockCommand;
import com.stockmanagement.inventory.application.dto.response.StockResponse;
import com.stockmanagement.inventory.application.event.DomainEventPublisher;
import com.stockmanagement.inventory.application.mapper.StockMapper;
import com.stockmanagement.inventory.domain.model.Stock;
import com.stockmanagement.inventory.domain.model.valueobject.*;
import com.stockmanagement.inventory.domain.repository.StockRepository;
import com.stockmanagement.inventory.application.service.WithdrawStockUseCase;
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
class WithdrawStockUseCaseTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockMapper stockMapper;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private WithdrawStockUseCase withdrawStockUseCase;

    private Stock stock;

    @BeforeEach
    void setUp() {
        stock = Stock.create(
                "P001",
                new ProductSKU("SKU-001"),
                new LocationId("L001"),
                UnitOfMeasure.PIECE);
        stock.receiveStock(Quantity.of(100), "Initial", "Admin");
    }

    @Test
    void shouldWithdrawStockSuccessfully() {
        // Arrange
        WithdrawStockCommand command = new WithdrawStockCommand(
                stock.getId().toString(), "10", "Engineering", "Project Usage", "Staff");

        when(stockRepository.findById(any(StockId.class)))
                .thenReturn(Optional.of(stock));
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);
        when(stockMapper.toResponse(any(Stock.class))).thenReturn(new StockResponse(
                "id", "SKU-001", "L001", "90", "0", "PIECE", 1L, "now", "now"));

        // Act
        withdrawStockUseCase.execute(command);

        // Assert
        verify(stockRepository).save(stock);
        verify(eventPublisher).publish(any());
    }
}
