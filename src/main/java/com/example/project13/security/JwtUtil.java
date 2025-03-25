package com.example.project13.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {
    private final SecretKey secretKey;
    private final long accessTokenValidity = 3600000;

    public JwtUtil() {
        // 512비트(64바이트) 이상의 키 생성
        String base64Key = "JY1Ar7jSHqb4cc6QcsNNUx2NHp4QAOHy";
        this.secretKey = Keys.hmacShaKeyFor(base64Key.getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(secretKey) // 변경된 부분
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey) // 변경된 부분
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // 변경된 부분
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
