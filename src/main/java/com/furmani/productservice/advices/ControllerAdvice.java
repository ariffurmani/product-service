package com.furmani.productservice.advices;

import com.furmani.productservice.dtos.ProductNotFoundDto;
import com.furmani.productservice.exceptions.ProductNotFoundException;
import org.springframework.stereotype.Controller;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {
    @org.springframework.web.bind.annotation.ExceptionHandler(ProductNotFoundException.class)
    public org.springframework.http.ResponseEntity<?> handleProductNotFoundException(ProductNotFoundException ex) {
        ProductNotFoundDto dto = new ProductNotFoundDto();
        dto.setMessage(ex.getMessage());

        return new org.springframework.http.ResponseEntity<>(dto, org.springframework.http.HttpStatus.NOT_FOUND);
    }
}
