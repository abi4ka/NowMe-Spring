package com.abik.nowme.module.shared.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class JwtService {

    private final String SECRET = "i4exmjh9lpa5wt9sve0lfm2y7vvs4ke1iil8zqf5";
    private final Algorithm algorithm = Algorithm.HMAC256(SECRET);

    public String generateToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 86400000))
                .sign(algorithm);
    }

    public String extractUsername(String token) {
        return JWT.require(algorithm)
                .build()
                .verify(token)
                .getSubject();
    }
}
