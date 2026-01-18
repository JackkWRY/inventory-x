package com.stockmanagement.inventory.domain.model.valueobject;

public record RoleId(String value) { // Using String ID for roles like 'role-admin'
    public RoleId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("RoleId cannot be null or empty");
        }
    }
}
