package com.stockmanagement.inventory.domain.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);

    Optional<Product> findById(UUID id);

    Optional<Product> findBySku(String sku);

    Page<Product> findAll(Pageable pageable);

    boolean existsBySku(String sku);

    Page<Product> search(String query, Pageable pageable);
}
