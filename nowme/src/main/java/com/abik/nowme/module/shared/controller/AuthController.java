package com.abik.nowme.module.shared.controller;

import com.abik.nowme.module.shared.service.AuthService;
import com.abik.nowme.module.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
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
    public UserDto.AuthResponse register(@RequestBody UserDto.RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public UserDto.AuthResponse login(@RequestBody UserDto.LoginRequest request) {
        return authService.login(request);
    }
}