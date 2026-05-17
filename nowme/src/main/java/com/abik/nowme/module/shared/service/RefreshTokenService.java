package com.abik.nowme.module.shared.service;

import com.abik.nowme.module.shared.entity.RefreshToken;
import com.abik.nowme.module.shared.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public void save(Long userId, String token, LocalDateTime expiresAt) {

        RefreshToken entity = RefreshToken.builder()
                .userId(userId)
                .token(token)
                .expiresAt(expiresAt)
                .build();

        repository.save(entity);
    }

    public boolean isValid(Long userId, String token) {

        RefreshToken entity = repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (!entity.getUserId().equals(userId)) {
            return false;
        }

        if (entity.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        return true;
    }

    public void deleteByToken(String token) {
        repository.findByToken(token)
                .ifPresent(repository::delete);
    }
}
