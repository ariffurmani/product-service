package com.furmani.productservice.services;

import com.furmani.productservice.dtos.FakeStoreProductDto;
import com.furmani.productservice.models.Product;

import java.util.List;

public interface ProductService {

    public FakeStoreProductDto createProduct(String name, Double price, String description, String category, String imageUrl);
    public Product getProductById(String id);
    public void deleteProduct(String name);
    public void updateProduct(String name, Double price, String description, String category, String imageUrl);
    public List<FakeStoreProductDto> getAllProducts();
}
