package com.stockmanagement.inventory.application.service;

import com.stockmanagement.inventory.domain.exception.DuplicateSkuException;
import com.stockmanagement.inventory.domain.exception.ProductNotFoundException;
import com.stockmanagement.inventory.domain.model.Product;
import com.stockmanagement.inventory.domain.model.ProductRepository;
import com.stockmanagement.inventory.presentation.dto.CreateProductRequest;
import com.stockmanagement.inventory.presentation.dto.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductManagementService {

    private final ProductRepository productRepository;

    @Transactional
    public Product createProduct(CreateProductRequest request) {
        log.info("Creating product with SKU: {}", request.sku());

        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateSkuException(request.sku());
        }

        Product product = Product.create(
                request.sku(),
                request.name(),
                request.description(),
                request.category(),
                request.price(),
                request.currency(),
                request.unitOfMeasure());

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(UUID id, UpdateProductRequest request) {
        log.info("Updating product ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id.toString()));

        product.updateDetails(request.name(), request.description(), request.category(), request.unitOfMeasure());
        product.updatePrice(request.price(), request.currency());

        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Product getProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id.toString()));
    }

    @Transactional(readOnly = true)
    public Page<Product> getProducts(String search, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return productRepository.search(search.trim(), pageable);
        }
        return productRepository.findAll(pageable);
    }
}
