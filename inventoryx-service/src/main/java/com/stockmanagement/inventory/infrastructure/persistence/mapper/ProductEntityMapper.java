package com.stockmanagement.inventory.infrastructure.persistence.mapper;

import com.stockmanagement.inventory.domain.model.Product;
import com.stockmanagement.inventory.infrastructure.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProductEntityMapper {

    public Product toDomain(ProductEntity entity) {
        if (entity == null)
            return null;

        return Product.reconstitute(
                UUID.fromString(entity.getId()),
                entity.getSku(),
                entity.getName(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getPriceAmount(),
                entity.getPriceCurrency(),
                entity.getUnitOfMeasure(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public ProductEntity toEntity(Product domain) {
        if (domain == null)
            return null;

        ProductEntity entity = new ProductEntity();
        entity.setId(domain.getId().toString());
        entity.setSku(domain.getSku());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setCategory(domain.getCategory());
        entity.setPriceAmount(domain.getPrice());
        entity.setPriceCurrency(domain.getCurrency());
        entity.setUnitOfMeasure(domain.getUnitOfMeasure());
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        return entity;
    }
}
