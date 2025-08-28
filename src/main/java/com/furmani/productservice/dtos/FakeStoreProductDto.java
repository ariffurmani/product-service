package com.furmani.productservice.dtos;

import com.furmani.productservice.models.Category;
import com.furmani.productservice.models.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FakeStoreProductDto {
    private String id;
    private String title;
    private Double price;
    private String description;
    private String category;
    private String image;

    public Product toProduct(){
        Product product = new Product();
        product.setId(this.id);
        product.setName(this.title);
        product.setPrice(this.price);
        product.setDescription(this.description);
        product.setImageUrl(this.image);
//        Category category = new Category();
//        category.getName(this.category);
//        product.setCategory(category);
        return product;
    }
}
