package com.stockmanagement.inventory.domain.model.valueobject;

public record Email(String value) {
    public Email {
        if (value == null || !value.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}
