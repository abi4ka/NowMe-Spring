package com.abik.nowme.module.nowme.dto;

import java.time.LocalDateTime;

public record NowmeResponse(
        Long id,
        Long userId,
        String description,
        LocalDateTime creationTime,
        Long likes,
        Long comments,
        String username,
        String userAvatar,
        Boolean favorite,
        Boolean liked
) {}
