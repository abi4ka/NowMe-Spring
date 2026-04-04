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

        String cleanToken = token.replace("Bearer ", "");

        followService.follow(cleanToken, userId);

        return ResponseEntity.ok("OK");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> unfollow(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId
    ) {
        String cleanToken = token.replace("Bearer ", "");

        followService.unfollow(cleanToken, userId);

        return ResponseEntity.ok("UNFOLLOW OK");
    }
}