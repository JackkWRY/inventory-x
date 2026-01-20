package com.stockmanagement.inventory.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record DashboardDataResponse(
        BigDecimal totalStockValue,
        long totalItems,
        long lowStockItems,
        long totalLocations,
        List<DashboardActivityDto> recentActivities) {
    public record DashboardActivityDto(
            String id,
            String type,
            BigDecimal quantity,
            String sku,
            String locationName,
            String performedBy,
            Instant performedAt) {
    }
}
