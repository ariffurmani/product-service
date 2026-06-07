package com.furmani.productservice.configurations;

import com.furmani.productservice.interceptors.ProductAuthorizationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final ProductAuthorizationInterceptor productAuthorizationInterceptor;

    public WebMvcConfiguration(ProductAuthorizationInterceptor productAuthorizationInterceptor) {
        this.productAuthorizationInterceptor = productAuthorizationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(productAuthorizationInterceptor)
                .addPathPatterns("/product/**");
    }
}


