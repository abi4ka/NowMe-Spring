package com.abik.nowme.module.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class UserDto {

    private Long id;

    public record RegisterRequest(String username, String password) {}
    public record LoginRequest(String username, String password) {}
    public record AuthResponse(String token) {}
}