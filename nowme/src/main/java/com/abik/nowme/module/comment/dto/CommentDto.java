package com.abik.nowme.module.comment.dto;

public record CommentDto(
        Long id,
        Long userId,
        String userAvatar,
        String username,
        String content
) {}