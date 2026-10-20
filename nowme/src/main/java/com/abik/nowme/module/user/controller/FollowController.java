package com.abik.nowme.module.user.controller;

import com.abik.nowme.config.CurrentUser;
import com.abik.nowme.module.user.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}")
    public ResponseEntity<Map<String, String>> follow(
            @CurrentUser Long followerId,
            @PathVariable Long userId
    ) {
        followService.follow(followerId, userId);
        return ResponseEntity.ok(Map.of("status", "FOLLOWED"));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> unfollow(
            @CurrentUser Long followerId,
            @PathVariable Long userId
    ) {
        followService.unfollow(followerId, userId);
        return ResponseEntity.ok(Map.of("status", "UNFOLLOWED"));
    }
}