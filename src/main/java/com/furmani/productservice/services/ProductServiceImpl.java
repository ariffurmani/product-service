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
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
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
        log.debug("Fetching all non-deleted products");
        List<Product> products = productRepository.findAllByIsDeletedFalse();
        log.debug("Fetched {} non-deleted products", products.size());
        return products;
    }

    @Override
    public Product getProductById(Long id) throws ProductNotFoundException, InvalidProductData {
        validateId(id, "Product id is required");
        log.debug("Fetching product by id={}", id);
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.warn("Product not found for id={}", id);
                    return new ProductNotFoundException("Product with id " + id + " not found");
                });
        log.debug("Fetched product successfully for id={}, name={}", id, product.getName());
        return product;
    }

     @Override
     public Product create(ProductRequestDto productRequestDto) throws InvalidProductData {

         log.info("Creating product: {}", describeProductRequest(productRequestDto));
         validateProductData(productRequestDto);

         Product product = new Product();
         product.setName(normalize(productRequestDto.getName()));
         product.setPrice(productRequestDto.getPrice());
         product.setDescription(productRequestDto.getDescription());
         product.setImageUrl(productRequestDto.getImageUrl());
         product.setStockQuantity(productRequestDto.getStockQuantity());
         product.setCategory(getCategoryByName(normalize(productRequestDto.getCategory())));
         Product savedProduct = productRepository.save(product);
         log.info("Product created successfully, id={}, name={}, category={}, stockQuantity={}", savedProduct.getId(), savedProduct.getName(), savedProduct.getCategory() != null ? savedProduct.getCategory().getName() : null, savedProduct.getStockQuantity());
         return savedProduct;
     }

     @Override
     public Product update(Long id, ProductRequestDto productRequestDto) throws ProductNotFoundException, InvalidProductData {
         validateId(id, "Product id is required for update");
         log.info("Updating product id={} with payload: {}", id, describeProductRequest(productRequestDto));

         Product product = productRepository.findByIdAndIsDeletedFalse(id)
                 .orElseThrow(() -> {
                     log.warn("Product not found for update, id={}", id);
                     return new ProductNotFoundException("Product with id " + id + " not found");
                 });

         validateProductData(productRequestDto);

         product.setName(normalize(productRequestDto.getName()));
         product.setPrice(productRequestDto.getPrice());
         product.setDescription(productRequestDto.getDescription());
         product.setImageUrl(productRequestDto.getImageUrl());
         product.setStockQuantity(productRequestDto.getStockQuantity());
         product.setCategory(getCategoryByName(normalize(productRequestDto.getCategory())));

         Product updatedProduct = productRepository.save(product);
         log.info("Product updated successfully, id={}, name={}, stockQuantity={}", updatedProduct.getId(), updatedProduct.getName(), updatedProduct.getStockQuantity());
         return updatedProduct;
     }

    @Override
    public void deleteProduct(Long id) throws ProductNotFoundException, InvalidProductData {
        validateId(id, "Product id is required for deletion");
        log.info("Deleting product id={}", id);
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.warn("Product not found for deletion, id={}", id);
                    return new ProductNotFoundException("Product with id " + id + " not found");
                });
        product.setDeleted(true);

        productRepository.save(product);
        log.info("Product soft-deleted successfully, id={}", id);
    }

    @Override
    public List<Product> getProductsByCategory(String category) throws CategoryNotFoundException, InvalidProductData {

        if (isBlank(category)) {
            log.warn("Category lookup requested with blank category name");
            throw new InvalidProductData("Category name is required");
        }

        String normalizedCategory = normalize(category);
        log.debug("Fetching products by category={}", normalizedCategory);
        Category category1 = categoryRepository.findByName(normalizedCategory)
                .orElseThrow(() -> {
                    log.warn("Category not found: {}", normalizedCategory);
                    return new CategoryNotFoundException("Category '" + normalizedCategory + "' not found");
                });

        List<Product> products = productRepository.findAllByCategoryAndIsDeletedFalse(category1);
        log.debug("Fetched {} products for category={}", products.size(), normalizedCategory);
        return products;
    }

    @Override
    public Product incrementStock(Long id, long quantity) throws ProductNotFoundException, InvalidProductData {
        validateId(id, "Product id is required for stock update");
        if (quantity <= 0) {
            log.warn("Validation failed: invalid increment quantity={}", quantity);
            throw new InvalidProductData("Quantity must be a positive value");
        }

        log.info("Incrementing stock for product id={}, quantity={}", id, quantity);
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.warn("Product not found for stock increment, id={}", id);
                    return new ProductNotFoundException("Product with id " + id + " not found");
                });

        long newQty = product.getStockQuantity() + quantity;
        product.setStockQuantity(newQty);
        Product updated = productRepository.save(product);
        log.info("Stock incremented successfully for product id={}, newStock={}", id, updated.getStockQuantity());
        return updated;
    }

    @Override
    public Product decrementStock(Long id, long quantity) throws ProductNotFoundException, InvalidProductData {
        validateId(id, "Product id is required for stock update");
        if (quantity <= 0) {
            log.warn("Validation failed: invalid decrement quantity={}", quantity);
            throw new InvalidProductData("Quantity must be a positive value");
        }

        log.info("Decrementing stock for product id={}, quantity={}", id, quantity);
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.warn("Product not found for stock decrement, id={}", id);
                    return new ProductNotFoundException("Product with id " + id + " not found");
                });

        long current = product.getStockQuantity();
        if (current - quantity < 0) {
            log.warn("Insufficient stock for product id={}, current={}, requested={}", id, current, quantity);
            throw new InvalidProductData("Insufficient stock for product id " + id);
        }

        product.setStockQuantity(current - quantity);
        Product updated = productRepository.save(product);
        log.info("Stock decremented successfully for product id={}, newStock={}", id, updated.getStockQuantity());
        return updated;
    }

      private void validateProductData(ProductRequestDto productRequestDto) throws InvalidProductData {
          if (productRequestDto == null) {
              log.warn("Validation failed: product data is null");
              throw new InvalidProductData("Product data is required");
          }
          if (isBlank(productRequestDto.getName())) {
              log.warn("Validation failed: product name is blank");
              throw new InvalidProductData("Product name is required");
          }
          if (productRequestDto.getPrice() == null || productRequestDto.getPrice() <= 0) {
              log.warn("Validation failed: invalid product price={}", productRequestDto.getPrice());
              throw new InvalidProductData("Product price must be a positive value");
          }
          if (isBlank(productRequestDto.getCategory())) {
              log.warn("Validation failed: product category is blank");
              throw new InvalidProductData("Product category is required");
          }
          if (productRequestDto.getStockQuantity() < 0) {
              log.warn("Validation failed: invalid stock quantity={}", productRequestDto.getStockQuantity());
              throw new InvalidProductData("Product stock quantity must be a non-negative value");
          }
      }

    private Category getCategoryByName(String name) {
        Optional<Category> categoryDb = categoryRepository.findByName(name);

        Category category = new Category();
        if (categoryDb.isEmpty()) {
            log.debug("Category not found, creating new category: {}", name);
            category.setName(name);
            category = categoryRepository.save(category);
            log.debug("Category created successfully, id={}, name={}", category.getId(), category.getName());
        } else {
            category = categoryDb.get();
            log.debug("Category found, id={}, name={}", category.getId(), category.getName());
        }
        return category;
    }

    private void validateId(Long id, String message) throws InvalidProductData {
        if (id == null) {
            log.warn("Validation failed: id is null - {}", message);
            throw new InvalidProductData(message);
        }
    }

    private String describeProductRequest(ProductRequestDto productRequestDto) {
        if (productRequestDto == null) {
            return "null";
        }
        return String.format("name=%s, price=%s, category=%s, description=%s, imageUrl=%s, stockQuantity=%s",
                productRequestDto.getName(),
                productRequestDto.getPrice(),
                productRequestDto.getCategory(),
                productRequestDto.getDescription(),
                productRequestDto.getImageUrl(),
                productRequestDto.getStockQuantity());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
