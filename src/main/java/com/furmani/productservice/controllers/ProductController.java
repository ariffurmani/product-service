package com.furmani.productservice.controllers;

import com.furmani.productservice.dtos.ProductRequestDto;
import com.furmani.productservice.dtos.StockUpdateDto;
import com.furmani.productservice.exceptions.CategoryNotFoundException;
import com.furmani.productservice.exceptions.InvalidProductData;
import com.furmani.productservice.exceptions.ProductNotFoundException;
import com.furmani.productservice.models.Product;
import com.furmani.productservice.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) throws ProductNotFoundException, InvalidProductData {
        log.info("GET /products/{} - request received", id);
        Product product = productService.getProductById(id);
        log.info("GET /products/{} - request completed successfully", id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        log.info("GET /products - request received");
        List<Product> products = productService.getAllProducts();
        log.info("GET /products - request completed successfully, count={}", products.size());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequestDto productRequestDto) throws InvalidProductData {
        log.info("POST /products - request received");
        Product createdProduct = productService.create(productRequestDto);
        log.info("POST /products - product created successfully, id={}, name={}", createdProduct.getId(), createdProduct.getName());
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDto productRequestDto) throws ProductNotFoundException, InvalidProductData {
        log.info("PUT /products/{} - request received", id);
        Product updatedProduct = productService.update(id, productRequestDto);
        log.info("PUT /products/{} - product updated successfully", id);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) throws ProductNotFoundException, InvalidProductData {
        log.info("DELETE /products/{} - request received", id);
        productService.deleteProduct(id);
        log.info("DELETE /products/{} - product deleted successfully", id);
        return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) throws CategoryNotFoundException, InvalidProductData {
        log.info("GET /products/category/{} - request received", category);
        List<Product> products = productService.getProductsByCategory(category);
        log.info("GET /products/category/{} - request completed successfully, count={}", category, products.size());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping("/{id}/stock/increment")
    public ResponseEntity<Product> incrementStock(@PathVariable Long id, @RequestBody StockUpdateDto stockUpdateDto) throws ProductNotFoundException, InvalidProductData {
        log.info("POST /product/{}/stock/increment - request received, quantity={}", id, stockUpdateDto != null ? stockUpdateDto.getQuantity() : null);
        Product product = productService.incrementStock(id, stockUpdateDto != null ? stockUpdateDto.getQuantity() : 0L);
        log.info("POST /product/{}/stock/increment - completed successfully, newStock={}", id, product.getStockQuantity());
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping("/{id}/stock/decrement")
    public ResponseEntity<Product> decrementStock(@PathVariable Long id, @RequestBody StockUpdateDto stockUpdateDto) throws ProductNotFoundException, InvalidProductData {
        log.info("POST /product/{}/stock/decrement - request received, quantity={}", id, stockUpdateDto != null ? stockUpdateDto.getQuantity() : null);
        Product product = productService.decrementStock(id, stockUpdateDto != null ? stockUpdateDto.getQuantity() : 0L);
        log.info("POST /product/{}/stock/decrement - completed successfully, newStock={}", id, product.getStockQuantity());
        return new ResponseEntity<>(product, HttpStatus.OK);
    }
}