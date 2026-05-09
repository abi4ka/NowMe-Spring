package com.abik.nowme.module.shared.service;

import com.abik.nowme.module.user.dto.AuthResponse;
import com.abik.nowme.module.user.dto.LoginRequest;
import com.abik.nowme.module.user.dto.RegisterRequest;
import com.abik.nowme.module.user.entity.User;
import com.abik.nowme.module.user.repository.UserRepository;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("USERNAME_ALREADY_EXISTS");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(request.password());
        userRepository.save(user);

        return generateTokens(user.getId());
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsernameAndActiveTrue(request.username())
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (!user.getPassword().equals(request.password())) {
            throw new RuntimeException("WRONG_PASSWORD");
        }

        return generateTokens(user.getId());
    }

    public AuthResponse refresh(String accessToken, String refreshToken) {

        Long userIdFromRefresh = jwtService.extractUserId(refreshToken);
        Long userIdFromAccess;

        try {
            userIdFromAccess = jwtService.extractUserId(accessToken);
        } catch (TokenExpiredException e) {
            userIdFromAccess = jwtService.extractUserIdIgnoringExpiration(accessToken);
        }

        if (!userIdFromRefresh.equals(userIdFromAccess)) {
            throw new RuntimeException("TOKEN_MISMATCH");
        }

        if (!"refresh".equals(jwtService.getTokenType(refreshToken))) {
            throw new RuntimeException("INVALID_REFRESH_TOKEN");
        }

        if (!refreshTokenService.isValid(userIdFromRefresh, refreshToken)) {
            throw new RuntimeException("INVALID_REFRESH_TOKEN");
        }

        if (!userRepository.existsByIdAndActiveTrue(userIdFromRefresh)) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

        refreshTokenService.deleteByToken(refreshToken);

        return generateTokens(userIdFromRefresh);
    }

    private AuthResponse generateTokens(Long userId) {

        String access = jwtService.generateAccessToken(userId);
        String refresh = jwtService.generateRefreshToken(userId);

        Date expiresAt = jwtService.getRefreshTokenExpiresAt();

        refreshTokenService.save(userId, refresh, expiresAt);

        return new AuthResponse(access, refresh);
    }
}
