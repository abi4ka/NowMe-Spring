package com.abik.nowme.module.nowme.dto;

import com.abik.nowme.module.user.Visibility;
import jakarta.validation.constraints.NotNull;

public record UpdateNowmeVisibilityRequest(
        @NotNull Visibility visibility
) {}
