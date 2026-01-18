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
        // Ensure secret is Base64 encoded or simple bytes?
        // Standard practice for HMAC-SHA is to use bytes.
        // If string is simple, use getBytes. If Base64, decode.
        // Assuming simple string for dev defaults, but safe decoding handles both
        // often.
        // For robustness, let's treat it as Base64 if possible or raw bytes.
        // Using Decoders.BASE64.decode(jwtSecret) expects Base64.
        // For default value, I should ensure it's Base64 or use
        // Keys.hmacShaKeyFor(secret.getBytes())
        // I will use `hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret))` but user must
        // provide Base64.
        // To be safe for the default dev key, I'll assume valid Base64 OR use raw
        // bytes.
        // Let's stick to standard practice: Secret should be Base64 encoded.
        // I will update the default value in the code to be a valid valid Base64 for
        // the long string,
        // or just use Keys.hmacShaKeyFor(jwtSecret.getBytes()) for simplicity in this
        // demo.
        // Using getBytes is safer for arbitrary strings.
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        // Note: I will need to update application.yml with a valid Base64 secret soon.
    }
}
