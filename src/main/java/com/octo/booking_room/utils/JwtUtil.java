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
    @Value("${jwt.secret:change-me-to-a-very-long-secret-at-least-32-bytes}")
    private String secret;

    private SecretKey key;

    private static final long EXPIRATION_TIME_MS = 43200000; // 12 hours

    private String expiration;

    @PostConstruct
    private void init() {
        // Keys.hmacShaKeyFor will throw an IllegalArgumentException if the
        // provided byte array is too short for the chosen algorithm (HS256 by
        // default).  Using @PostConstruct lets us fail early with a clear
        // message instead of a cascading unsatisfied dependency.
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
