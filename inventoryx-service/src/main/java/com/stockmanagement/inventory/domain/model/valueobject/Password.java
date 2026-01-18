package com.stockmanagement.inventory.domain.model.valueobject;

public record Password(String value) {
    public Password {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be empty");
        }
    }
}
