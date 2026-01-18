package com.stockmanagement.inventory.domain.repository;

import com.stockmanagement.inventory.domain.model.AuditLog;

public interface AuditLogRepository {
    void save(AuditLog auditLog);
}
