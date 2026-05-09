package com.abik.nowme.module.user.controller;

import com.abik.nowme.module.user.dto.UpdateAvatarRequest;
import com.abik.nowme.module.user.dto.UserProfileResponse;
import com.abik.nowme.module.user.dto.UserSearchResponse;
import com.abik.nowme.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserProfileResponse getMyProfile(@RequestHeader("Authorization") String token) {
        return userService.getMyProfile(token);
    }

    @GetMapping("/{id}")
    public UserProfileResponse getProfileById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        return userService.getProfileById(token, id);
    }

    @GetMapping("/username/{username}")
    public UserProfileResponse getProfileByUsername(
            @RequestHeader("Authorization") String token,
            @PathVariable String username
    ) {
        return userService.getProfileByUsername(token, username);
    }

    @GetMapping("/search")
    public List<UserSearchResponse> searchUsers(
            @RequestHeader("Authorization") String token,
            @RequestParam String query
    ) {
        return userService.searchUsers(token, query);
    }

    @PutMapping("/avatar")
    public ResponseEntity<Void> updateAvatar(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateAvatarRequest request
    ) {
        userService.updateAvatar(token, request.avatar());
        return ResponseEntity.ok().build();
    }
}
