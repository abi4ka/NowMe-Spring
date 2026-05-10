package com.abik.nowme.module.user.dto;

import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        String username,
        String avatar,
        LocalDateTime registerTime,
        long followersCount,
        long followingCount,
        long friends,
        long streakDays,
        boolean me,
        boolean following,
        boolean friend
) {}
