package com.abik.nowme.module.user.controller;

import com.abik.nowme.module.user.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}")
    public ResponseEntity<String> follow(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId
    ) {

        // limpiar token
        String cleanToken = token.replace("Bearer ", "");

        followService.follow(cleanToken, userId);

        return ResponseEntity.ok("OK");
    }
}