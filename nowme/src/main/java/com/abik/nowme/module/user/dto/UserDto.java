package com.abik.nowme.module.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
public class UserDto {

    private Long id;

    public record RegisterRequest(String username, String password) {}
    public record LoginRequest(String username, String password) {}
    public record AuthResponse(String accessToken, String refreshToken) {}

    public record RefreshRequest(String accessToken, String refreshToken) {}

    public record ProfileResponse(
            Long id,
            String username,
            String avatar,
            LocalDateTime registerTime,
            long followersCount,
            long followingCount,
            long friends,
            boolean me,
            boolean following,
            boolean friend
    ) {}
}
