package com.abik.nowme.module.shared.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Value;

@Service
public class JwtService {

    private final Algorithm algorithm;

    private static final long ACCESS_EXP = 1000 * 60 * 15 * 8;      // 2h
    private static final long REFRESH_EXP = 1000L * 60 * 60 * 24 * 7 * 4; // 28days
    private static final int MIN_SECRET_LENGTH = 32;

    public JwtService(@Value("${security.jwt.secret:}") String secret) {
        if (secret == null || secret.isBlank() || secret.length() < MIN_SECRET_LENGTH) {
            throw new IllegalStateException(
                    "security.jwt.secret must be set (JWT_SECRET env or .env file) and be at least "
                            + MIN_SECRET_LENGTH + " characters long");
        }
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public String generateAccessToken(Long userId) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("type", "access")
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_EXP))
                .sign(algorithm);
    }

    public String generateRefreshToken(Long userId) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("type", "refresh")
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_EXP))
                .sign(algorithm);
    }

    public LocalDateTime getRefreshTokenExpiresAt() {
        return LocalDateTime.now().plus(Duration.ofMillis(REFRESH_EXP));
    }

    public DecodedJWT verify(String token) {
        return JWT.require(algorithm).build().verify(token);
    }

    public String extractUsername(String token) {
        return verify(token).getSubject();
    }

    public String getTokenType(String token) {
        return verify(token).getClaim("type").asString();
    }

    public Long extractUserId(String token) {
        return Long.parseLong(
                verify(token).getSubject()
        );
    }

    public Long extractUserIdIgnoringExpiration(String token) {
        try {
            String subject = JWT.decode(token).getSubject();
            return Long.parseLong(subject);
        } catch (JWTDecodeException | NumberFormatException e) {
            throw new RuntimeException("INVALID_TOKEN", e);
        }
    }

    public String normalizeBearerToken(String token) {
        if (token == null) {
            throw new RuntimeException("TOKEN_REQUIRED");
        }
        return token.startsWith("Bearer ") ? token.substring(7) : token;
    }

    public Long getUserIdFromToken(String token) {
        DecodedJWT jwt = verify(normalizeBearerToken(token));
        if (!"access".equals(jwt.getClaim("type").asString())) {
            throw new RuntimeException("INVALID_TOKEN");
        }
        return Long.parseLong(jwt.getSubject());
    }
}
