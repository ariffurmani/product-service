package com.furmani.productservice.controllers;

import com.furmani.productservice.dtos.ProductRequestDto;
import com.furmani.productservice.exceptions.InvalidProductData;
import com.furmani.productservice.exceptions.ProductNotFoundException;
import com.furmani.productservice.models.Product;
import com.furmani.productservice.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    ProductService productService;

    ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Long id) throws ProductNotFoundException {
        Product product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping("products")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping("products")
    public Product createProduct(@RequestBody ProductRequestDto productRequestDto) throws InvalidProductData {
        return productService.create(productRequestDto);
    }

    @PutMapping("products/{id}")
    public Product updateProduct(@PathVariable("id") Long id, @RequestBody ProductRequestDto productRequestDto) throws ProductNotFoundException, InvalidProductData {
        return productService.update(id, productRequestDto);
    }

    @DeleteMapping("products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id) throws ProductNotFoundException {
        productService.deleteProduct(id);
        return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
    }
}