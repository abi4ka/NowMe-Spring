package com.abik.nowme.module.user.controller;

import com.abik.nowme.module.user.dto.UserDto;
import com.abik.nowme.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserDto.ProfileResponse getMyProfile(@RequestHeader("Authorization") String token) {
        return userService.getMyProfile(token);
    }

    @GetMapping("/{id}")
    public UserDto.ProfileResponse getProfileById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        return userService.getProfileById(token, id);
    }

    @GetMapping("/username/{username}")
    public UserDto.ProfileResponse getProfileByUsername(
            @RequestHeader("Authorization") String token,
            @PathVariable String username
    ) {
        return userService.getProfileByUsername(token, username);
    }

    @GetMapping("/search")
    public List<UserDto.SearchResponse> searchUsers(
            @RequestHeader("Authorization") String token,
            @RequestParam String query
    ) {
        return userService.searchUsers(token, query);
    }
}
