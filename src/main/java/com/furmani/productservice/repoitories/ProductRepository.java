package com.furmani.productservice.repoitories;

import com.furmani.productservice.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findProductById(Long id);
    Optional<Product> findByName(String name);
    Optional<Product> getByIdAndIsDeleted(Long id);
}
