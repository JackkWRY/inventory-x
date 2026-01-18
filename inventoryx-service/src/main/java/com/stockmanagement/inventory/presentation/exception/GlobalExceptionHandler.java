package com.stockmanagement.inventory.presentation.exception;

import com.stockmanagement.inventory.domain.exception.*;
import com.stockmanagement.inventory.presentation.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler - Handles all exceptions globally.
 * 
 * PURPOSE: Convert domain exceptions to HTTP responses
 * 
 * PATTERN: @RestControllerAdvice
 * - Applies to all @RestController
 * - Centralized exception handling
 * - Consistent error responses
 * 
 * LOGGING STRATEGY:
 * - WARN: Expected business errors (not found, insufficient stock)
 * - ERROR: Unexpected system errors (internal error)
 * 
 * @author InventoryX Development Team
 * @since 2026-01-12
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        /**
         * Handles StockNotFoundException.
         * Returns 404 NOT FOUND.
         */
        @ExceptionHandler(StockNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleStockNotFound(
                        StockNotFoundException ex) {
                log.warn("Stock not found: {}", ex.getMessage());
                ErrorResponse error = new ErrorResponse(
                                "STOCK_NOT_FOUND",
                                ex.getMessage(),
                                Instant.now());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        /**
         * Handles InsufficientStockException.
         * Returns 409 CONFLICT.
         */
        @ExceptionHandler(InsufficientStockException.class)
        public ResponseEntity<ErrorResponse> handleInsufficientStock(
                        InsufficientStockException ex) {
                log.warn("Insufficient stock: {}", ex.getMessage());
                ErrorResponse error = new ErrorResponse(
                                "INSUFFICIENT_STOCK",
                                ex.getMessage(),
                                Instant.now());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        /**
         * Handles InvalidStockOperationException.
         * Returns 400 BAD REQUEST.
         */
        @ExceptionHandler(InvalidStockOperationException.class)
        public ResponseEntity<ErrorResponse> handleInvalidOperation(
                        InvalidStockOperationException ex) {
                log.warn("Invalid stock operation: {}", ex.getMessage());
                ErrorResponse error = new ErrorResponse(
                                "INVALID_OPERATION",
                                ex.getMessage(),
                                Instant.now());
                return ResponseEntity.badRequest().body(error);
        }

        /**
         * Handles validation errors from @Valid.
         * Returns 400 BAD REQUEST.
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationErrors(
                        MethodArgumentNotValidException ex) {
                String message = ex.getBindingResult().getFieldErrors().stream()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .collect(Collectors.joining(", "));

                log.warn("Validation failed: {}", message);
                ErrorResponse error = new ErrorResponse(
                                "VALIDATION_ERROR",
                                message,
                                Instant.now());
                return ResponseEntity.badRequest().body(error);
        }

        /**
         * Handles Authentication errors.
         * Returns 401 UNAUTHORIZED.
         */
        @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthenticationException(
                        org.springframework.security.core.AuthenticationException ex) {
                log.warn("Authentication failed: {}", ex.getMessage());
                ErrorResponse error = new ErrorResponse(
                                "UNAUTHORIZED",
                                ex.getMessage(),
                                Instant.now());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        /**
         * Handles all other exceptions.
         * Returns 500 INTERNAL SERVER ERROR.
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
                // Log the exception with full stack trace for debugging
                log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

                ErrorResponse error = new ErrorResponse(
                                "INTERNAL_ERROR",
                                "An unexpected error occurred",
                                Instant.now());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
}
