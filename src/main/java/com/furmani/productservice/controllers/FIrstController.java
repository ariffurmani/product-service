package com.furmani.productservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FIrstController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Product Service";
    }
}
