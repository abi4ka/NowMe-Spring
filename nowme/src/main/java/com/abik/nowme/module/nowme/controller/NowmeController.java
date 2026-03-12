package com.abik.nowme.module.nowme.controller;

import com.abik.nowme.module.nowme.dto.CreateNowmeDto;
import com.abik.nowme.module.nowme.service.NowmeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nowme")
public class NowmeController {

    private final NowmeService nowmeService;

    @PostMapping
    public Long createNowme(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateNowmeDto request) {

        Long userId = extractUserIdFromToken(token);

        return nowmeService.createNowme(userId, request);
    }

    private Long extractUserIdFromToken(String token) {
        //TODO: JWT token
        return 1L;
    }
}