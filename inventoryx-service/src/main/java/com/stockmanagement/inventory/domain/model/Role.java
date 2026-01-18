package com.stockmanagement.inventory.domain.model;

import com.stockmanagement.inventory.domain.model.valueobject.RoleId;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Role {
    private final RoleId id;
    private final String name;
    private final String description;
    private final Set<Permission> permissions;

    public Role(RoleId id, String name, String description, Set<Permission> permissions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.permissions = permissions != null ? permissions : new HashSet<>();
    }

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }
}
