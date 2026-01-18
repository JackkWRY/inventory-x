package com.stockmanagement.inventory.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", schema = "inventory")
@Getter
@Setter
@NoArgsConstructor
public class AuditLogEntity {
    @Id
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(nullable = false)
    private String action;

    private String resource;

    @Column(name = "resource_id")
    private String resourceId;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    private String status;

    @Column(name = "performed_at")
    private LocalDateTime performedAt;

    @PrePersist
    protected void onCreate() {
        if (performedAt == null) {
            performedAt = LocalDateTime.now();
        }
    }
}
