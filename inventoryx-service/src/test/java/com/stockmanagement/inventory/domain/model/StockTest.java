package com.stockmanagement.inventory.domain.model;

import com.stockmanagement.inventory.domain.model.valueobject.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockTest {

    @Test
    void shouldCreateNewStockCorrectly() {
        Stock stock = Stock.create(
                "PROD-001",
                new ProductSKU("SKU-001"),
                new LocationId("LOC-A"),
                UnitOfMeasure.PIECE);

        assertThat(stock).isNotNull();
        assertThat(stock.getProductId()).isEqualTo("PROD-001");
        assertThat(stock.getSku().value()).isEqualTo("SKU-001");
        assertThat(stock.getLocationId().value()).isEqualTo("LOC-A");
        // Quantity.value() returns BigDecimal. ZERO is BigDecimal.ZERO.
        assertThat(stock.getAvailableQuantity().value()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldReceiveStock() {
        Stock stock = Stock.create(
                "PROD-001",
                new ProductSKU("SKU-001"),
                new LocationId("LOC-A"),
                UnitOfMeasure.PIECE);

        // Quantity.of(long)
        stock.receiveStock(Quantity.of(50), "Initial Stock", "Admin");

        assertThat(stock.getAvailableQuantity().value()).isEqualByComparingTo(BigDecimal.valueOf(50));
    }

    @Test
    void shouldWithdrawStock() {
        Stock stock = Stock.create(
                "PROD-001",
                new ProductSKU("SKU-001"),
                new LocationId("LOC-A"),
                UnitOfMeasure.PIECE);

        // Setup initial stock
        stock.receiveStock(Quantity.of(100), "Initial", "Admin");

        stock.withdraw(Quantity.of(30), "Sales", "Sold", "Staff");

        assertThat(stock.getAvailableQuantity().value()).isEqualByComparingTo(BigDecimal.valueOf(70));
    }

    @Test
    void shouldThrowExceptionWhenWithdrawingMoreThanAvailable() {
        Stock stock = Stock.create(
                "PROD-001",
                new ProductSKU("SKU-001"),
                new LocationId("LOC-A"),
                UnitOfMeasure.PIECE);
        stock.receiveStock(Quantity.of(10), "Initial", "Admin");

        // Need to use Quantity.of(20)
        assertThatThrownBy(() -> stock.withdraw(Quantity.of(20), "Sort", "Test", "Staff"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient stock");
    }
}
