package com.stockmanagement.inventory.infrastructure.persistence.repository;

import com.stockmanagement.inventory.domain.model.AuditLog;
import com.stockmanagement.inventory.domain.repository.AuditLogRepository;
import com.stockmanagement.inventory.infrastructure.persistence.entity.AuditLogEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuditLogRepositoryImpl implements AuditLogRepository {

    private final JpaAuditLogRepository jpaAuditLogRepository;

    @Override
    public void save(AuditLog auditLog) {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(auditLog.getId().toString());
        entity.setUserId(auditLog.getUserId() != null ? auditLog.getUserId().value().toString() : null);
        entity.setAction(auditLog.getAction());
        entity.setResource(auditLog.getResource());
        entity.setResourceId(auditLog.getResourceId());
        entity.setDetails(auditLog.getDetails());
        entity.setIpAddress(auditLog.getIpAddress());
        entity.setUserAgent(auditLog.getUserAgent());
        entity.setStatus(auditLog.getStatus());
        entity.setPerformedAt(auditLog.getPerformedAt());

        jpaAuditLogRepository.save(entity);
    }
}
