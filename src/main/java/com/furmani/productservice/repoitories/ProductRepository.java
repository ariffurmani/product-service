package com.furmani.productservice.repoitories;

import com.furmani.productservice.models.Category;
import com.furmani.productservice.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByCategoryAndIsDeletedFalse(Category category);
    // Create a method to search product with id and isDeleted = false
    Optional<Product> findByIdAndIsDeletedFalse(Long id);
    // create a method to get all products where isDeleted = false
    List<Product> findAllByIsDeletedFalse();
}
