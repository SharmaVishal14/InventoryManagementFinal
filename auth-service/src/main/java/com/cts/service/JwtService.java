package com.cts.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;


import com.cts.entity.UserCredentials;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
@Service
public class JwtService {

    // Your preset secret value (should be loaded from configuration in production)
    public static final String SECRET = "FAwq03T8oLdM38iVlWZqEsKNG3fjfa6SRbPGzMC1hQn/Sf7M/IKjP3TVUz9uOIYCA5z2nNJx436/tMnSAKQCow==";

    public String generateToken(UserCredentials userCredentials) {
        Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

        Map<String, Object> claims = new HashMap<>();
        claims.put("password", userCredentials.getPassword());
        claims.put("email", userCredentials.getEmail());

        return createToken(claims, userCredentials, key);
    }

    public String createToken(Map<String, Object> claims, UserCredentials userCredentials, Key key) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userCredentials.getEmail()) // Identifies the user
                .setIssuedAt(new Date()) // Token creation time
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24-hour expiration
                .signWith(key, SignatureAlgorithm.HS256) // Securing the token
                .compact();
    }

    public void validateToken(String token) {
        Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
         Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
