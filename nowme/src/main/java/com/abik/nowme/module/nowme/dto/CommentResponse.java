package com.abik.nowme.module.nowme.dto;

public record CommentResponse(
        Long id,
        Long userId,
        String userAvatar,
        String username,
        String content
) {}
