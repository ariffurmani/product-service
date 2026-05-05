package com.furmani.productservice.services;

import com.furmani.productservice.dtos.ProductRequestDto;
import com.furmani.productservice.exceptions.CategoryNotFoundException;
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
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAllByIsDeletedFalse();
    }

    @Override
    public Product getProductById(Long id) throws ProductNotFoundException, InvalidProductData {
        validateId(id, "Product id is required");
        return productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
    }

    @Override
    public Product create(ProductRequestDto productRequestDto) throws InvalidProductData {

        // Validate product data
        validateProductData(productRequestDto);

        // Create new product
        Product product = new Product();
        product.setName(normalize(productRequestDto.getName()));
        product.setPrice(productRequestDto.getPrice());
        product.setDescription(productRequestDto.getDescription());
        product.setImageUrl(productRequestDto.getImageUrl());
        product.setCategory(getCategoryByName(normalize(productRequestDto.getCategory())));
        return productRepository.save(product);
    }

    @Override
    public Product update(Long id, ProductRequestDto productRequestDto) throws ProductNotFoundException, InvalidProductData {
        validateId(id, "Product id is required for update");

        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));

        // Validate product data
        validateProductData(productRequestDto);

        // Update product details
        product.setName(normalize(productRequestDto.getName()));
        product.setPrice(productRequestDto.getPrice());
        product.setDescription(productRequestDto.getDescription());
        product.setImageUrl(productRequestDto.getImageUrl());
        product.setCategory(getCategoryByName(normalize(productRequestDto.getCategory())));

        // Save updated product
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) throws ProductNotFoundException, InvalidProductData {
        validateId(id, "Product id is required for deletion");
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
        product.setDeleted(true);

        productRepository.save(product);
    }

    @Override
    public List<Product> getProductsByCategory(String category) throws CategoryNotFoundException, InvalidProductData {

        if (isBlank(category)) {
            throw new InvalidProductData("Category name is required");
        }

        String normalizedCategory = normalize(category);
        Category category1 = categoryRepository.findByName(normalizedCategory)
                .orElseThrow(() -> new CategoryNotFoundException("Category '" + normalizedCategory + "' not found"));

        return productRepository.findAllByCategoryAndIsDeletedFalse(category1);
    }

    private void validateProductData(ProductRequestDto productRequestDto) throws InvalidProductData {
        if (productRequestDto == null) {
            throw new InvalidProductData("Product data is required");
        }
        if (isBlank(productRequestDto.getName())) {
            throw new InvalidProductData("Product name is required");
        }
        if (productRequestDto.getPrice() == null || productRequestDto.getPrice() <= 0) {
            throw new InvalidProductData("Product price must be a positive value");
        }
        if (isBlank(productRequestDto.getCategory())) {
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

    private void validateId(Long id, String message) throws InvalidProductData {
        if (id == null) {
            throw new InvalidProductData(message);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
