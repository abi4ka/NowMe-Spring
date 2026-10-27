package com.abik.nowme.service;

import com.abik.nowme.module.shared.service.JwtService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private static final String SECRET = "unit-test-secret-0123456789-abcdefghij";

    private final JwtService jwtService = new JwtService(SECRET);

    @Test
    void accessToken_ResolvesUserId() {
        String token = jwtService.generateAccessToken(42L);

        assertEquals(42L, jwtService.getUserIdFromToken("Bearer " + token));
    }

    @Test
    void refreshToken_IsRejectedAsAccessToken() {
        String refresh = jwtService.generateRefreshToken(42L);

        assertThrows(RuntimeException.class, () -> jwtService.getUserIdFromToken("Bearer " + refresh));
    }

    @Test
    void tokenSignedWithAnotherSecret_IsRejected() {
        String forged = JWT.create()
                .withSubject("1")
                .withClaim("type", "access")
                .withExpiresAt(new Date(System.currentTimeMillis() + 60_000))
                .sign(Algorithm.HMAC256("i4exmjh9lpa5wt9sve0lfm2y7vvs4ke1iil8zqf5"));

        assertThrows(RuntimeException.class, () -> jwtService.getUserIdFromToken("Bearer " + forged));
    }

    @Test
    void tokensIssuedInTheSameSecond_AreDifferent() {
        assertNotEquals(jwtService.generateRefreshToken(42L), jwtService.generateRefreshToken(42L));
    }

    @Test
    void missingOrShortSecret_FailsFast() {
        assertThrows(IllegalStateException.class, () -> new JwtService(""));
        assertThrows(IllegalStateException.class, () -> new JwtService("too-short"));
    }
}
