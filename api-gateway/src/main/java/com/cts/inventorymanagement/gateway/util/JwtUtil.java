package com.cts.inventorymanagement.gateway.util;


import java.nio.charset.StandardCharsets;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;



@Service
public class JwtUtil {

    // Your preset secret value (should be loaded from configuration in production)
    public static final String SECRET = "FAwq03T8oLdM38iVlWZqEsKNG3fjfa6SRbPGzMC1hQn/Sf7M/IKjP3TVUz9uOIYCA5z2nNJx436/tMnSAKQCow==";

    

    public void validateToken(String token) {
        Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
         Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
