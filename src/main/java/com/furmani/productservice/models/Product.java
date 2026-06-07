package com.furmani.productservice.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Product extends BaseModel{
    private String name;
    private Double price;
    private String description;
    @ManyToOne(cascade = CascadeType.ALL)
    @JsonBackReference
    private Category category;
    private String imageUrl;
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long stockQuantity = 0L;
}
