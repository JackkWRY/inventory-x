package com.stockmanagement.inventory.infrastructure.persistence;

import com.stockmanagement.inventory.domain.model.Product;
import com.stockmanagement.inventory.domain.model.ProductRepository;
import com.stockmanagement.inventory.infrastructure.persistence.entity.ProductEntity;
import com.stockmanagement.inventory.infrastructure.persistence.mapper.ProductEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaRepository;
    private final ProductEntityMapper mapper;

    @Override
    public Product save(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        ProductEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaRepository.findById(id.toString())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return jpaRepository.findBySku(sku)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsBySku(String sku) {
        return jpaRepository.existsBySku(sku);
    }

    @Override
    public Page<Product> search(String query, Pageable pageable) {
        return jpaRepository
                .findBySkuContainingIgnoreCaseOrNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(query, query,
                        query, pageable)
                .map(mapper::toDomain);
    }
}
