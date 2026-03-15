package com.octo.booking_room.utils;

import java.util.Date;

import jakarta.annotation.PostConstruct;
import lombok.Getter;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Utility class for JSON Web Token operations.  The signing key is derived from
 * a secret configured in application properties.  A minimum key length of
 * 256 bits (32 bytes) is required by the JWT library; using a shorter
 * constant previously caused a bean-instantiation failure during startup.
 */
@Component
@Getter
public class JwtUtil {

    // injected from configuration so it can be changed per environment
    @Value("${jwt.secret:}")
    private String secret;

    private SecretKey key;

    private static final long EXPIRATION_TIME_MS = 43200000; // 12 hours

    private String expiration;

    @PostConstruct
    private void init() {
        // Ensure a secure, non-empty secret of minimum length before building key.
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret is not configured. Set jwt.secret in application.yaml or JWT_SECRET environment variable.");
        }
        if (secret.getBytes().length < 32) {
            throw new IllegalStateException("JWT secret is too short. It must be at least 32 bytes long.");
        }

        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email) {
        Date ex = new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS);

        expiration = ex.toString();
        return Jwts.builder()
            .subject(email)
            .issuedAt(new Date())
            .expiration(ex)
            .signWith(key)
            .compact();
    }

    public String getSubjectFromToken(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
