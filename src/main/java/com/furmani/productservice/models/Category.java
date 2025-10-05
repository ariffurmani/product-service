package com.furmani.productservice.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Category extends BaseModel {
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<Product>  products;
}
