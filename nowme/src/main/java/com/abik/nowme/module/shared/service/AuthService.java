package com.abik.nowme.module.shared.service;

import com.abik.nowme.module.user.dto.UserDto;
import com.abik.nowme.module.user.entity.User;
import com.abik.nowme.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public void register(UserDto.RegisterRequest request) {

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(request.password());

        userRepository.save(user);
    }

    public UserDto.AuthResponse login(UserDto.LoginRequest request) {

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.password())) {
            throw new RuntimeException("Wrong password");
        }

        String token = jwtService.generateToken(user.getUsername());

        return new UserDto.AuthResponse(token);
    }
}
