package com.stockmanagement.inventory.infrastructure.persistence.repository;

import com.stockmanagement.inventory.infrastructure.persistence.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAuditLogRepository extends JpaRepository<AuditLogEntity, String> {
}
