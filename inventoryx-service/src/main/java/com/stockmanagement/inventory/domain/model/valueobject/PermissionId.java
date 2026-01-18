package com.stockmanagement.inventory.domain.model.valueobject;

public record PermissionId(String value) { // Using String ID for permissions like 'perm-stock-read'
    public PermissionId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PermissionId cannot be null or empty");
        }
    }
}
