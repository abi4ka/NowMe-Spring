package com.abik.nowme.service;

import com.abik.nowme.module.shared.service.AuthService;
import com.abik.nowme.module.shared.service.JwtService;
import com.abik.nowme.module.shared.service.RefreshTokenService;
import com.abik.nowme.module.user.dto.AuthResponse;
import com.abik.nowme.module.user.dto.LoginRequest;
import com.abik.nowme.module.user.dto.RegisterRequest;
import com.abik.nowme.module.user.entity.User;
import com.abik.nowme.module.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, jwtService, refreshTokenService, passwordEncoder);
    }

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest("testuser", "securepass");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("securepass")).thenReturn("encoded_pass");
        when(jwtService.generateAccessToken(any())).thenReturn("mock_access_token");
        when(jwtService.generateRefreshToken(any())).thenReturn("mock_refresh_token");
        when(jwtService.getRefreshTokenExpiresAt()).thenReturn(LocalDateTime.now().plusDays(7));

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mock_access_token", response.accessToken());
        assertEquals("mock_refresh_token", response.refreshToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_DuplicateUsername_ThrowsException() {
        RegisterRequest request = new RegisterRequest("existinguser", "securepass");
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertEquals("USERNAME_ALREADY_EXISTS", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest("testuser", "securepass");
        User user = new User();
        user.setId(42L);
        user.setUsername("testuser");
        user.setPassword("encoded_pass");

        when(userRepository.findByUsernameAndActiveTrue("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("securepass", "encoded_pass")).thenReturn(true);
        when(jwtService.generateAccessToken(42L)).thenReturn("mock_access_token");
        when(jwtService.generateRefreshToken(42L)).thenReturn("mock_refresh_token");
        when(jwtService.getRefreshTokenExpiresAt()).thenReturn(LocalDateTime.now().plusDays(7));

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock_access_token", response.accessToken());
        assertEquals("mock_refresh_token", response.refreshToken());
    }

    @Test
    void login_WrongPassword_ThrowsException() {
        LoginRequest request = new LoginRequest("testuser", "wrongpass");
        User user = new User();
        user.setId(42L);
        user.setUsername("testuser");
        user.setPassword("encoded_pass");

        when(userRepository.findByUsernameAndActiveTrue("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "encoded_pass")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("WRONG_PASSWORD", exception.getMessage());
    }

    @Test
    void logout_CallsRefreshTokenService() {
        authService.logout("refresh_token_to_delete");
        verify(refreshTokenService, times(1)).deleteByToken("refresh_token_to_delete");
    }
}
