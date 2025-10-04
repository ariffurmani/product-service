package com.furmani.productservice.services;

import com.furmani.productservice.dtos.ProductRequestDto;
import com.furmani.productservice.exceptions.InvalidProductData;
import com.furmani.productservice.exceptions.ProductNotFoundException;
import com.furmani.productservice.models.Product;

import java.util.List;

public interface ProductService {

    List<Product> getAllProducts();
    Product getProductById(Long id) throws ProductNotFoundException;
    Product create(ProductRequestDto  productRequestDto) throws InvalidProductData;
    Product update(Long id, ProductRequestDto productRequestDto) throws ProductNotFoundException, InvalidProductData;
    void deleteProduct(Long id) throws ProductNotFoundException;
}
