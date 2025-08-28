package com.furmani.productservice.services;

import com.furmani.productservice.dtos.FakeStoreProductDto;
import com.furmani.productservice.dtos.ProductRequestDto;
import com.furmani.productservice.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FakeStoreProductServiceImpl implements ProductService {

    @Autowired
    RestTemplate restTemplate;

    @Override
    public FakeStoreProductDto createProduct(String name, Double price, String description, String category, String imageUrl) {
        FakeStoreProductDto dto = new FakeStoreProductDto();
        dto.setTitle(name);
        dto.setPrice(price);
        dto.setDescription(description);
        dto.setCategory(category);
        dto.setImage(imageUrl);

        FakeStoreProductDto responseDto = restTemplate.postForObject("https://fakestoreapi.com/products", dto, FakeStoreProductDto.class);

       return responseDto;
    }

    @Override
    public Product getProductById(String id) {
        FakeStoreProductDto dto = restTemplate.getForObject ("https://fakestoreapi.com/products/" + id, FakeStoreProductDto.class);
        return dto.toProduct();
    }

    @Override
    public void deleteProduct(String name) {

    }

    @Override
    public void updateProduct(String name, Double price, String description, String category, String imageUrl) {

    }

    @Override
    public List<FakeStoreProductDto> getAllProducts() {
        // method returns array of products retreived from https://fakestoreapi.com/products
        FakeStoreProductDto[] dtos = restTemplate.getForObject("https://fakestoreapi.com/products", FakeStoreProductDto[].class);
        return List.of(dtos);
    }
}
