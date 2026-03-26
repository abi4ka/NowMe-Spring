package com.abik.nowme.module.shared.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RefreshTokenService {

    private final Map<String, String> storage = new ConcurrentHashMap<>();
    // username -> refreshToken

    public void save(String username, String refreshToken) {
        storage.put(username, refreshToken);
    }

    public boolean isValid(String username, String token) {
        return token.equals(storage.get(username));
    }

    public void delete(String username) {
        storage.remove(username);
    }
}