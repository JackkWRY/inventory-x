package com.stockmanagement.inventory.application.service;

import com.stockmanagement.inventory.application.dto.DashboardDataResponse;
import com.stockmanagement.inventory.infrastructure.persistence.entity.StockMovementEntity;
import com.stockmanagement.inventory.infrastructure.persistence.JpaLocationRepository;
import com.stockmanagement.inventory.infrastructure.persistence.repository.JpaStockMovementRepository;
import com.stockmanagement.inventory.infrastructure.persistence.repository.JpaStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final JpaStockRepository stockRepository;
    private final JpaStockMovementRepository stockMovementRepository;
    private final JpaLocationRepository locationRepository;

    @Transactional(readOnly = true)
    public DashboardDataResponse getDashboardData() {
        BigDecimal totalValue = stockRepository.calculateTotalStockValue();
        if (totalValue == null) {
            totalValue = BigDecimal.ZERO;
        }

        long totalItems = stockRepository.count();

        // Low stock threshold (configurable via application properties in production)
        final BigDecimal LOW_STOCK_THRESHOLD = new BigDecimal("10");
        long lowStockCount = stockRepository.countByAvailableQuantityLessThan(LOW_STOCK_THRESHOLD);

        long totalLocations = locationRepository.count();

        List<StockMovementEntity> recentMovements = stockMovementRepository.findTop10ByOrderByPerformedAtDesc();
        List<DashboardDataResponse.DashboardActivityDto> activityDtos = recentMovements.stream()
                .map(this::mapToActivityDto)
                .collect(Collectors.toList());

        return new DashboardDataResponse(
                totalValue,
                totalItems,
                lowStockCount,
                totalLocations,
                activityDtos);
    }

    private DashboardDataResponse.DashboardActivityDto mapToActivityDto(StockMovementEntity entity) {
        String sku = "Unknown";
        String locationName = "Unknown";

        if (entity.getStock() != null) {
            sku = entity.getStock().getSku();
            // Returns location ID; frontend maps to display name
            locationName = entity.getStock().getLocationId();
        }

        return new DashboardDataResponse.DashboardActivityDto(
                entity.getId(),
                entity.getMovementType(),
                entity.getQuantity(),
                sku,
                locationName,
                entity.getPerformedBy(),
                entity.getPerformedAt());
    }
}
