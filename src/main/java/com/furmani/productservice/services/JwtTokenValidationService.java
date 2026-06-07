package com.furmani.productservice.services;

import com.furmani.productservice.exceptions.InvalidTokenException;
import com.furmani.productservice.security.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class JwtTokenValidationService implements TokenValidationService {

    private final String secretKeyValue;

    public JwtTokenValidationService(@Value("${jwt.secret-key:}") String secretKeyValue) {
        this.secretKeyValue = secretKeyValue;
    }

    @Override
    public AuthenticatedUser validateToken(String token) throws InvalidTokenException {
        log.info("Validating token");

        if (token == null || token.trim().isEmpty()) {
            log.warn("Token validation failed: token is null or empty");
            throw new InvalidTokenException("Token cannot be null or empty");
        }

        if (secretKeyValue == null || secretKeyValue.trim().isEmpty()) {
            log.error("JWT secret key is not configured");
            throw new InvalidTokenException("JWT secret key is not configured");
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(buildSecretKey(secretKeyValue.trim()))
                    .build()
                    .parseSignedClaims(token.trim())
                    .getPayload();

            Object expObject = claims.get("exp");
            if (expObject == null) {
                log.warn("Token validation failed: exp claim is missing");
                throw new InvalidTokenException("Token is missing expiry claim");
            }

            long expiryDateMillis;
            try {
                expiryDateMillis = ((Number) expObject).longValue() * 1000L;
            } catch (ClassCastException e) {
                log.warn("Token validation failed: exp claim is not a valid number");
                throw new InvalidTokenException("Token has invalid expiry format");
            }

            long currentDate = System.currentTimeMillis();
            if (expiryDateMillis <= currentDate) {
                log.warn("Token validation failed: token has expired. Expiry: {}, Current: {}", expiryDateMillis, currentDate);
                throw new InvalidTokenException("Token has expired");
            }

            String email = (String) claims.get("email");
            if (email == null || email.trim().isEmpty()) {
                log.warn("Token validation failed: email claim is missing");
                throw new InvalidTokenException("Token is missing email claim");
            }

            AuthenticatedUser authUser = new AuthenticatedUser();
            authUser.setEmail(email.trim());
            authUser.setRoles(extractRoles(claims.get("roles")));

            log.info("Token validation successful for user: {}", email);
            return authUser;
        } catch (InvalidTokenException e) {
            throw e;
        } catch (JwtException e) {
            log.warn("Token validation failed: JWT parsing error", e);
            throw new InvalidTokenException("Invalid token: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("An unexpected error occurred during token validation", e);
            throw new RuntimeException("Token validation failed: " + e.getMessage(), e);
        }
    }

    private List<String> extractRoles(Object rolesObject) {
        List<String> roleNames = new ArrayList<>();
        if (rolesObject instanceof List<?> rolesList) {
            for (Object role : rolesList) {
                if (role instanceof Map<?, ?> roleMap) {
                    Object value = roleMap.get("value");
                    if (value != null) {
                        roleNames.add(value.toString());
                    }
                } else if (role != null) {
                    roleNames.add(role.toString());
                }
            }
        }
        return roleNames;
    }

    private SecretKey buildSecretKey(String secretKeyValue) {
        try {
            byte[] decoded = Decoders.BASE64.decode(secretKeyValue);
            return Keys.hmacShaKeyFor(decoded);
        } catch (IllegalArgumentException ignored) {
            return Keys.hmacShaKeyFor(secretKeyValue.getBytes(StandardCharsets.UTF_8));
        }
    }
}


