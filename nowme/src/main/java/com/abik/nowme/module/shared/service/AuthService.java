package com.abik.nowme.module.shared.service;

import com.abik.nowme.module.user.dto.UserDto;
import com.abik.nowme.module.user.entity.User;
import com.abik.nowme.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public UserDto.AuthResponse register(UserDto.RegisterRequest request) {

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(request.password());
        userRepository.save(user);

        return generateTokens(user.getUsername());
    }

    public UserDto.AuthResponse login(UserDto.LoginRequest request) {

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.password())) {
            throw new RuntimeException("Wrong password");
        }

        return generateTokens(user.getUsername());
    }

    public UserDto.AuthResponse refresh(String accessToken, String refreshToken) {

        String usernameFromRefresh = jwtService.extractUsername(refreshToken);
        String usernameFromAccess = jwtService.extractUsername(accessToken);

        if (!usernameFromRefresh.equals(usernameFromAccess)) {
            throw new RuntimeException("Token mismatch");
        }

        if (!"refresh".equals(jwtService.getTokenType(refreshToken))) {
            throw new RuntimeException("Invalid refresh token");
        }

        if (!refreshTokenService.isValid(usernameFromRefresh, refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        return generateTokens(usernameFromRefresh);
    }

    private UserDto.AuthResponse generateTokens(String username) {
        String access = jwtService.generateAccessToken(username);
        String refresh = jwtService.generateRefreshToken(username);

        refreshTokenService.save(username, refresh);

        return new UserDto.AuthResponse(access, refresh);
    }
}