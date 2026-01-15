package com.stockmanagement.inventory.application.dto.response;

import java.util.List;

/**
 * PagedStockResponse - Paginated response DTO for stock queries.
 * 
 * BEST PRACTICE: Pagination Response
 * ===================================
 * Provides metadata alongside data for proper pagination handling.
 * 
 * FIELDS:
 * - content: List of stocks in current page
 * - page: Current page number (0-indexed)
 * - size: Number of items per page
 * - totalElements: Total number of items across all pages
 * - totalPages: Total number of pages
 * - first: Whether this is the first page
 * - last: Whether this is the last page
 * 
 * @author InventoryX Development Team
 * @since 2026-01-14
 */
public record PagedStockResponse(
        List<StockResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last) {

    /**
     * Create from Spring Data Page object.
     * 
     * @param springPage Spring Data Page
     * @param content    Mapped content list
     * @return PagedStockResponse
     */
    public static PagedStockResponse from(
            org.springframework.data.domain.Page<?> springPage,
            List<StockResponse> content) {
        return new PagedStockResponse(
                content,
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getTotalPages(),
                springPage.isFirst(),
                springPage.isLast());
    }
}
