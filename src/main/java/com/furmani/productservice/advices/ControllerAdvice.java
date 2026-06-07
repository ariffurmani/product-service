package com.furmani.productservice.advices;

import com.furmani.productservice.dtos.ProductNotFoundDto;
import com.furmani.productservice.exceptions.CategoryNotFoundException;
import com.furmani.productservice.exceptions.InvalidProductData;
import com.furmani.productservice.exceptions.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ProductNotFoundDto> handleProductNotFoundException(ProductNotFoundException ex) {
        log.warn("Product not found: {}", ex.getMessage());
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ProductNotFoundDto> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        log.warn("Category not found: {}", ex.getMessage());
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidProductData.class)
    public ResponseEntity<ProductNotFoundDto> handleInvalidProductData(InvalidProductData ex) {
        log.warn("Invalid product data: {}", ex.getMessage());
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ProductNotFoundDto> buildResponse(String message, HttpStatus status) {
        ProductNotFoundDto dto = new ProductNotFoundDto();
        dto.setMessage(message);
        return new ResponseEntity<>(dto, status);
    }
}
