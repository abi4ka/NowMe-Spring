package com.abik.nowme.module.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9_.]{3,20}$",
                message = "must be 3-20 characters: letters, digits, '_' or '.'")
        String username,

        @NotBlank
        @Size(min = 8, max = 24)
        String password
) {}
