package com.furmani.productservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestDto {
    private String name;
    private Double price;
    private String description;
    private String category;
    private String imageUrl;

}
