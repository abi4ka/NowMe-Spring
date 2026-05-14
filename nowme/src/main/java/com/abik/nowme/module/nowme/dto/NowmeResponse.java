package com.abik.nowme.module.nowme.dto;

import com.abik.nowme.module.user.Visibility;
import java.time.LocalDateTime;

public record NowmeResponse(
        Long id,
        Long userId,
        String description,
        LocalDateTime creationTime,
        Visibility visibility,
        Long likes,
        Long comments,
        String username,
        String userAvatar,
        Boolean owner,
        Boolean favorite,
        Boolean liked
) {}
