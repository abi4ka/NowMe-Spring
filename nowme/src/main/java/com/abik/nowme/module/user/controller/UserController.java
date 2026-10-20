package com.abik.nowme.module.user.controller;

import com.abik.nowme.config.CurrentUser;
import com.abik.nowme.module.user.dto.UpdateAvatarRequest;
import com.abik.nowme.module.user.dto.UserProfileResponse;
import com.abik.nowme.module.user.dto.UserSearchResponse;
import com.abik.nowme.module.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserProfileResponse getMyProfile(@CurrentUser Long currentUserId) {
        return userService.getMyProfile(currentUserId);
    }

    @GetMapping("/{id}")
    public UserProfileResponse getProfileById(
            @CurrentUser Long currentUserId,
            @PathVariable Long id
    ) {
        return userService.getProfileById(currentUserId, id);
    }

    @GetMapping("/username/{username}")
    public UserProfileResponse getProfileByUsername(
            @CurrentUser Long currentUserId,
            @PathVariable String username
    ) {
        return userService.getProfileByUsername(currentUserId, username);
    }

    @GetMapping("/search")
    public Page<UserSearchResponse> searchUsers(
            @CurrentUser Long currentUserId,
            @RequestParam String query,
            Pageable pageable
    ) {
        return userService.searchUsers(currentUserId, query, pageable);
    }

    @PutMapping("/avatar")
    public ResponseEntity<Void> updateAvatar(
            @CurrentUser Long currentUserId,
            @Valid @RequestBody UpdateAvatarRequest request
    ) {
        userService.updateAvatar(currentUserId, request.avatar());
        return ResponseEntity.ok().build();
    }
}
