package com.abik.nowme.module.nowme.dto;

import java.time.LocalDateTime;

public record NowmeDTO(
        Long id,
        String description,
        LocalDateTime creationTime,
        Long likes,
        Long comments,
        String username,
        String userAvatar
) {}
