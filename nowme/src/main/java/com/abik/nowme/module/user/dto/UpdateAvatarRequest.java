package com.abik.nowme.module.user.dto;

import com.abik.nowme.module.shared.validation.SingleEmoji;
import jakarta.validation.constraints.NotBlank;

public record UpdateAvatarRequest(
        @NotBlank
        @SingleEmoji
        String avatar
) {}
