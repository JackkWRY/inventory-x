package com.stockmanagement.inventory.domain.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String idOrSku) {
        super("Product not found: " + idOrSku);
    }
}
