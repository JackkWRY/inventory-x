package com.stockmanagement.inventory.domain.model;

import com.stockmanagement.inventory.domain.model.valueobject.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class AuditLog {
    private final UUID id;
    private final UserId userId; // Nullable (system action)
    private final String action;
    private final String resource;
    private final String resourceId;
    private final String details;
    private final String ipAddress;
    private final String userAgent;
    private final String status;
    private final LocalDateTime performedAt;
}
