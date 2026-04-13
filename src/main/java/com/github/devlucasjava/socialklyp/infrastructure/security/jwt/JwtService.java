package com.github.devlucasjava.socialklyp.infrastructure.security.jwt;

import com.github.devlucasjava.socialklyp.domain.entity.User;
import com.github.devlucasjava.socialklyp.domain.enuns.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    // Valores lidos de jwt.accessExpiration e jwt.refreshExpiration no application.yaml
    @Value("${jwt.accessExpiration}")
    private long accessExpiration;

    @Value("${jwt.refreshExpiration}")
    private long refreshExpiration;


    public String generateAccessToken(User user) {
        return buildToken(user, accessExpiration);
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, refreshExpiration);
    }

//    private String buildToken(User user, long expirationSeconds) { // TODO: For Production
//        Instant now = Instant.now();
//
//        List<String> roles = user.getRoles()
//                .stream()
//                .map(Role::name)
//                .toList();
//
//        JwtClaimsSet claims = JwtClaimsSet.builder()
//                .subject(user.getUsername())
//                .issuedAt(now)
//                .expiresAt(now.plusSeconds(expirationSeconds))
//                .claim("email", user.getEmail())
//                .claim("roles", roles)
//                .build();
//
//        return jwtEncoder
//                .encode(JwtEncoderParameters.from(claims))
//                .getTokenValue();
//    }

    private String buildToken(User user, long expirationSeconds) { // TODO: For Development with HS256 explicit in header token
        Instant now = Instant.now();

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::name)
                .toList();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationSeconds))
                .claim("email", user.getEmail())
                .claim("roles", roles)
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();

    }


    public Instant getExpirationToken(String token) {
        return jwtDecoder.decode(token).getExpiresAt();
    }

    public boolean isTokenValid(String token, User user) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getSubject().equals(user.getUsername());
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractSubject(String token) {
        return jwtDecoder.decode(token).getSubject();
    }
}