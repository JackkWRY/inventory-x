package com.stockmanagement.inventory.infrastructure.persistence.repository;

import com.stockmanagement.inventory.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaRoleRepository extends JpaRepository<RoleEntity, String> {
    Optional<RoleEntity> findByName(String name);
}
