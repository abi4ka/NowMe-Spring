package com.abik.nowme.module.nowme.dto;

public record CommentDto(
        Long id,
        Long userId,
        String userAvatar,
        String username,
        String content
) {}