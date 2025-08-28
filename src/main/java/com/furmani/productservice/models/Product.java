package com.furmani.productservice.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {
    private String id;
    private String name;
    private Double price;
    private String description;
    private Category category;
    private String imageUrl;
}
