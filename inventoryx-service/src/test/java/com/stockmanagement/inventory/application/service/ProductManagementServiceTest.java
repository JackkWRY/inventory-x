package com.stockmanagement.inventory.application.service;

import com.stockmanagement.inventory.domain.model.Product;
import com.stockmanagement.inventory.domain.model.ProductRepository;
import com.stockmanagement.inventory.domain.exception.DuplicateSkuException;
import com.stockmanagement.inventory.presentation.dto.CreateProductRequest;
import com.stockmanagement.inventory.presentation.dto.UpdateProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductManagementServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductManagementService productManagementService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.create(
                "SKU-001", "Product A", "Description", "Category",
                new BigDecimal("100.00"), "USD", "PIECE");
    }

    @Test
    void shouldCreateProductSuccessfully() {
        // Arrange
        CreateProductRequest request = new CreateProductRequest(
                "SKU-002", "Product B", "Desc", "Cat",
                new BigDecimal("50.00"), "USD", "PIECE");

        when(productRepository.existsBySku("SKU-002")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product created = productManagementService.createProduct(request);

        // Assert
        assertNotNull(created);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldFailCreateIfSkuExists() {
        // Arrange
        CreateProductRequest request = new CreateProductRequest(
                "SKU-001", "Product A", "Desc", "Cat",
                new BigDecimal("100.00"), "USD", "PIECE");

        when(productRepository.existsBySku("SKU-001")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateSkuException.class, () -> productManagementService.createProduct(request));
    }

    @Test
    void shouldUpdateProduct() {
        // Arrange
        UUID productId = product.getId();
        UpdateProductRequest request = new UpdateProductRequest(
                "Updated Product", "New Desc", "New Cat",
                new BigDecimal("150.00"), "USD", "PIECE");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product updated = productManagementService.updateProduct(productId, request);

        // Assert
        assertEquals("Updated Product", updated.getName());
        assertEquals(new BigDecimal("150.00"), updated.getPrice());
    }

    @Test
    void shouldGetProductsPaged() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // Act
        Page<Product> result = productManagementService.getProducts(null, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
    }
}
