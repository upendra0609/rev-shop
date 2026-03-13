package com.revshop.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private static final String SECRET =
            "revshop_secret_key_revshop_secret_key_123456789";

    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET.getBytes());


    // Generate Token
    public String generateToken(String email) {

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis()
                                + 1000 * 60 * 60 * 10)
                )
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    // Extract Email
    public String extractEmail(String token) {

        return extractClaims(token).getSubject();
    }


    // Validate Token
    public boolean validateToken(String token) {

        return !isExpired(token);
    }


    private boolean isExpired(String token) {

        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }


    // ✅ FIXED PARSER (LATEST VERSION)
    private Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
