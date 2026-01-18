package com.stockmanagement.inventory.domain.model.valueobject;

public record Username(String value) {
    public Username {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (value.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters");
        }
    }
}
