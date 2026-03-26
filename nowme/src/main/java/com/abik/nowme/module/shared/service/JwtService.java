package com.abik.nowme.module.shared.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class JwtService {

    private final String SECRET = "i4exmjh9lpa5wt9sve0lfm2y7vvs4ke1iil8zqf5";
    private final Algorithm algorithm = Algorithm.HMAC256(SECRET);

    private static final long ACCESS_EXP = 1000 * 60 * 15 * 8;      // 2h
    private static final long REFRESH_EXP = 1000L * 60 * 60 * 24 * 7 * 4; // 28days

    public String generateAccessToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withClaim("type", "access")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_EXP))
                .sign(algorithm);
    }

    public String generateRefreshToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withClaim("type", "refresh")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_EXP))
                .sign(algorithm);
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
}
