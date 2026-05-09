package com.abik.nowme.module.shared.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class JwtService {

    private final String SECRET = "i4exmjh9lpa5wt9sve0lfm2y7vvs4ke1iil8zqf5";
    private final Algorithm algorithm = Algorithm.HMAC256(SECRET);

    private static final long ACCESS_EXP = 1000 * 60 * 15 * 8;      // 2h
    private static final long REFRESH_EXP = 1000L * 60 * 60 * 24 * 7 * 4; // 28days

    public String generateAccessToken(Long userId) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("type", "access")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_EXP))
                .sign(algorithm);
    }

    public String generateRefreshToken(Long userId) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("type", "refresh")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_EXP))
                .sign(algorithm);
    }

    public Date getRefreshTokenExpiresAt() {
        return new Date(System.currentTimeMillis() + REFRESH_EXP);
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
}
