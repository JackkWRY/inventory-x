package com.stockmanagement.inventory.domain.model;

import com.stockmanagement.inventory.domain.model.valueobject.PermissionId;
import lombok.Getter;

@Getter
public class Permission {
    private final PermissionId id;
    private final String name;
    private final String description;
    private final String resource;
    private final String action;

    public Permission(PermissionId id, String name, String description, String resource, String action) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.resource = resource;
        this.action = action;
    }
}
