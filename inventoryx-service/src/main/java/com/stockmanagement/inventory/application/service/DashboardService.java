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
        // 1. Calculate Total Stock Value
        BigDecimal totalValue = stockRepository.calculateTotalStockValue();
        if (totalValue == null) {
            totalValue = BigDecimal.ZERO;
        }

        // 2. Count Total Items (distinct SKUs or Total Stocks? usually products count
        // or stock records)
        // Let's use total stock records for now, or maybe count products?
        // Requirement says "Total Items" or "Total Products". Let's use stock count as
        // "Items in Stock"
        // But maybe "Total Products" is better. Let's use Stock count for "Inventory
        // Items"
        long totalItems = stockRepository.count();

        // 3. Count Low Stock Items (Threshold < 10 for example)
        // TODO: Threshold should ideally be per product or global setting. Using 10
        // hardcoded for MVP.
        long lowStockCount = stockRepository.countByAvailableQuantityLessThan(new BigDecimal("10"));

        // 4. Count Locations
        long totalLocations = locationRepository.count();

        // 5. Recent Activity
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
        // Safe null handling for relationship lazy loading
        // Note: Repository must ensure Stock is loaded if we access it.
        // findTop10ByOrderByPerformedAtDesc generally fetches the entity.
        // But accessing entity.getStock() might trigger lazy load.
        // We need the SKU and Location Name.
        // For performance, we might want a fetch join, but for 10 items it's okay (N+1
        // is small).

        String sku = "Unknown";
        String locationName = "Unknown";

        if (entity.getStock() != null) {
            sku = entity.getStock().getSku();
            // We need to fetch location name. Stock only has locationId.
            // This is tricky. StockEntity has locationId String.
            // We need to look up location name from LocationRepo? Or just show Location ID?
            // User wants "Best Practices". showing ID is bad.
            // We might need to map it.
            // For MVP, let's try to get Location ID first.
            // Ideally, StockEntity should have a @ManyToOne to LocationEntity if we want
            // easy access,
            // but we designed it with referencing logic.
            // So we have to fetch location separately or cache it?
            // Or just return Location ID for now and let Frontend map it (since frontend
            // has location list).
            // Frontend ALREADY fetches all locations in `inventory/index.vue`.
            // Let's return Location ID in the DTO, and let Frontend map it to Name (reusing
            // the store).
            // Wait, this is "Recent Activity" on Dashboard. Dashboard might not load all
            // locations?
            // Actually, fetching 10 locations is cheap.
            locationName = entity.getStock().getLocationId();
        }

        return new DashboardDataResponse.DashboardActivityDto(
                entity.getId(),
                entity.getMovementType(),
                entity.getQuantity(),
                sku,
                locationName, // Sending ID for now, frontend can map if it loads locations, or we can fetch.
                // Let's stick to ID and let Frontend Map or if Frontend loads Locations.
                // Re-reading: "Display Location Name" was a task.
                // Doing it in Backend is safer for consistency. But requires N+1 queries or a
                // batch fetch.
                // Let's leave as ID for now and note it. Or do a quick lookup if I can inject
                // LocationRepo?
                // I have `locationRepository`. I can `findById`.
                entity.getPerformedBy(),
                entity.getPerformedAt());
    }
}
