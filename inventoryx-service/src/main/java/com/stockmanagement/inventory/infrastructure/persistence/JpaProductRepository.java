package com.stockmanagement.inventory.infrastructure.persistence;

import com.stockmanagement.inventory.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, String> {
    Optional<ProductEntity> findBySku(String sku);

    boolean existsBySku(String sku);

    Page<ProductEntity> findBySkuContainingIgnoreCaseOrNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            String sku, String name, String category, Pageable pageable);
}
