package com.stockmanagement.inventory.presentation.rest;

import com.stockmanagement.inventory.application.service.ProductManagementService;
import com.stockmanagement.inventory.domain.model.Product;
import com.stockmanagement.inventory.presentation.dto.CreateProductRequest;
import com.stockmanagement.inventory.presentation.dto.ProductResponse;
import com.stockmanagement.inventory.presentation.dto.UpdateProductRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductManagementService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<ProductResponse> products = productService.getProducts(search, pageable)
                .map(ProductResponse::fromDomain);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID id) {
        Product product = productService.getProduct(id);
        return ResponseEntity.ok(ProductResponse.fromDomain(product));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product created = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.fromDomain(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request) {
        Product updated = productService.updateProduct(id, request);
        return ResponseEntity.ok(ProductResponse.fromDomain(updated));
    }
}
