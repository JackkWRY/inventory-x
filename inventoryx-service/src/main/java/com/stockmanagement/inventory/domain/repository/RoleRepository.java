package com.stockmanagement.inventory.domain.repository;

import com.stockmanagement.inventory.domain.model.Role;
import com.stockmanagement.inventory.domain.model.valueobject.RoleId;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findById(RoleId id);

    Optional<Role> findByName(String name);
}
