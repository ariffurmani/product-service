package com.furmani.productservice.controllers;

import com.furmani.productservice.dtos.FakeStoreProductDto;
import com.furmani.productservice.dtos.ProductRequestDto;
import com.furmani.productservice.models.Product;
import com.furmani.productservice.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("products/{id}")
    public Product getProductById(@PathVariable("id") String id) {
        return productService.getProductById(id);
    }

    @GetMapping("products")
    public List<Product> getAllProducts() {
        List<FakeStoreProductDto> fakeProductList = productService.getAllProducts();
        List<Product> productList = fakeProductList.stream().map(FakeStoreProductDto::toProduct).toList();
        return productList;
    }

    @PostMapping("products")
    public FakeStoreProductDto createProduct(@RequestBody ProductRequestDto productRequestDto) {
        return productService.createProduct(productRequestDto.getName(), productRequestDto.getPrice(), productRequestDto.getDescription(), productRequestDto.getCategory(), productRequestDto.getImageUrl());
    }
}