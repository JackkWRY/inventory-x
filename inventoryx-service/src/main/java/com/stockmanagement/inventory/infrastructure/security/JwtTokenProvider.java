package com.stockmanagement.inventory.infrastructure.security;

import com.stockmanagement.inventory.domain.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${security.jwt.secret:inventoryx-secret-key-must-be-at-least-256-bits-long-make-sure-to-change-in-prod}")
    private String jwtSecret;

    @Value("${security.jwt.expiration-ms:900000}") // 15 mins
    private long jwtExpirationMs;

    @Value("${security.jwt.refresh-expiration-ms:604800000}") // 7 days
    private long refreshExpirationMs;

    public String generateToken(User user) {
        return buildToken(new HashMap<>(), user, jwtExpirationMs);
    }

    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user, refreshExpirationMs);
    }

    private String buildToken(Map<String, Object> extraClaims, User user, long expiration) {
        // Add roles to claims if needed, usually just subject (username) is enough if
        // we load details from DB
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername().value())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username)) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw e;
        }
    }

    private Key getSignInKey() {
        // Use UTF-8 bytes for HMAC-SHA key generation
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
