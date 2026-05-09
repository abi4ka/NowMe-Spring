package com.abik.nowme.module.shared.controller;

import com.abik.nowme.module.shared.service.AuthService;
import com.abik.nowme.module.user.dto.AuthResponse;
import com.abik.nowme.module.user.dto.LoginRequest;
import com.abik.nowme.module.user.dto.RefreshRequest;
import com.abik.nowme.module.user.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(
                authService.refresh(
                        request.accessToken(),
                        request.refreshToken()
                )
        );
    }
}
