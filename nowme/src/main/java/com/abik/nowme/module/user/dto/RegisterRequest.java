package com.abik.nowme.module.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Size(max = 20)
        String username,

        @NotBlank
        @Size(max = 24)
        String password
) {}
