package com.github.devlucasjava.socialklyp.infrastructure.security.jwt;

import com.github.devlucasjava.socialklyp.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expireIn}")
    private long jwtExpireIn;

    @Value("${jwt.refreshExpireIn}")
    private long refreshExpireIn;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private String generateToken(User user, Map<String, Object> claims, long expireIn) {

        Instant now = Instant.now();

        return Jwts.builder()
                .id(user.getId().toString())
                .subject(user.getEmail())
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expireIn)))
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenNotExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public Instant extractExpiration(String token) {
        return extractClaims(token).getExpiration().toInstant();
    }

    public boolean isValidToken(String token, User user) {
        return extractUsername(token).equals(user.getEmail()) &&
                isTokenNotExpired(token);
    }

    public String generateAccessToken(User user) {
        Map<String, Object> claims = Map.of(
                "email", user.getEmail(),
                "username", user.getUsername(),
                "role", user.getRole()
        );
        return generateToken(user, claims, jwtExpireIn);
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = Map.of(
                "username", user.getUsername()
        );
        return generateToken(user, claims, refreshExpireIn);
    }
}