package com.furmani.productservice.services;

import com.furmani.productservice.dtos.ProductRequestDto;
import com.furmani.productservice.exceptions.InvalidProductData;
import com.furmani.productservice.exceptions.ProductNotFoundException;
import com.furmani.productservice.models.Category;
import com.furmani.productservice.models.Product;
import com.furmani.productservice.repoitories.CategoryRepository;
import com.furmani.productservice.repoitories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SelfProductService implements ProductService {

    ProductRepository  productRepository;
    CategoryRepository categoryRepository;

    SelfProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) throws ProductNotFoundException {
        Optional<Product> product = productRepository.findProductById(id);
        if (product.isEmpty()) {
            throw new ProductNotFoundException("Product not found");
        }
        return product.get();
    }

    @Override
    public Product create(ProductRequestDto productRequestDto) throws InvalidProductData {

        // Validate product data
        validateProductData(productRequestDto);

        // Create new product
        Product product = new Product();
        product.setName(productRequestDto.getName());
        product.setPrice(productRequestDto.getPrice());
        product.setDescription(productRequestDto.getDescription());
        product.setImageUrl(productRequestDto.getImageUrl());
        product.setCategory(getCategoryByName(productRequestDto.getCategory()));
        return productRepository.save(product);
    }

    @Override
    public Product update(Long id, ProductRequestDto productRequestDto) throws ProductNotFoundException, InvalidProductData {
        if (id == null) {
            throw new InvalidProductData("The Product to be updated must have a valid id");
        }

        // Check if product exists
        Optional<Product> productDb = productRepository.findProductById(id);
        if (productDb.isEmpty()) {
            throw new ProductNotFoundException("Product not found");
        }

        // Validate product data
        validateProductData(productRequestDto);

        // Update product details
        Product product = productDb.get();
        product.setName(productRequestDto.getName());
        product.setPrice(productRequestDto.getPrice());
        product.setDescription(productRequestDto.getDescription());
        product.setImageUrl(productRequestDto.getImageUrl());
        product.setCategory(getCategoryByName(productRequestDto.getCategory()));

        // Save updated product
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(String name) throws ProductNotFoundException {
        if (name == null) {
            throw new ProductNotFoundException("The Product to be deleted must have a valid name");
        }
        Optional<Product> productDb = productRepository.findByName(name);
        if (productDb.isEmpty()) {
            throw new ProductNotFoundException("Product not found");
        }
        Product product = productDb.get();
        product.setDeleted(true);

        productRepository.save(product);
    }

    private void validateProductData(ProductRequestDto productRequestDto) throws InvalidProductData {
        if (productRequestDto.getName() == null || productRequestDto.getName().isEmpty()) {
            throw new InvalidProductData("Product name is required");
        }
        if (productRequestDto.getPrice() == null || productRequestDto.getPrice() < 0) {
            throw new InvalidProductData("Product price must be a positive value");
        }
        if (productRequestDto.getCategory() == null || productRequestDto.getCategory().isEmpty()) {
            throw new InvalidProductData("Product category is required");
        }
    }

    private Category getCategoryByName(String name) {
        Optional<Category> categoryDb = categoryRepository.findByName(name);

        Category category = new Category();
        if (categoryDb.isEmpty()) {
            category.setName(name);
            categoryRepository.save(category);
        } else {
            category = categoryDb.get();
        }
        return category;
    }
}
