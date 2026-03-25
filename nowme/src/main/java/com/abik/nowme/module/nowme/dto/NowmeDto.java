package com.abik.nowme.module.nowme.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
public class NowmeDto {
    public record NowmeDTO(
            Long id,
            String description,
            LocalDateTime creationTime,
            Long likes,
            Long comments,
            String username,
            String userAvatar
    ) {}
}
