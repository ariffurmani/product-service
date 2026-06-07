package com.furmani.productservice.services;

import com.furmani.productservice.exceptions.InvalidTokenException;
import com.furmani.productservice.security.AuthenticatedUser;

public interface TokenValidationService {
    AuthenticatedUser validateToken(String token) throws InvalidTokenException;
}


