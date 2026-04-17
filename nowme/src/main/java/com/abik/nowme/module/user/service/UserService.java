package com.abik.nowme.module.user.service;

import com.abik.nowme.module.shared.service.JwtService;
import com.abik.nowme.module.user.dto.UserDto;
import com.abik.nowme.module.user.entity.User;
import com.abik.nowme.module.user.repository.UserFollowRepository;
import com.abik.nowme.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;
    private final JwtService jwtService;

    public UserDto.ProfileResponse getMyProfile(String token) {
        Long userId = extractUserId(token);
        User user = getUserById(userId);

        return toProfileResponse(user, userId);
    }

    public UserDto.ProfileResponse getProfileById(String token, Long userId) {
        Long viewerId = extractUserId(token);
        requireActiveUser(viewerId);
        User user = getUserById(userId);

        return toProfileResponse(user, viewerId);
    }

    public UserDto.ProfileResponse getProfileByUsername(String token, String username) {
        Long viewerId = extractUserId(token);
        requireActiveUser(viewerId);
        User user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        return toProfileResponse(user, viewerId);
    }

    private UserDto.ProfileResponse toProfileResponse(User user, Long viewerId) {
        Long userId = user.getId();
        boolean me = userId.equals(viewerId);
        boolean following = !me && userFollowRepository.existsByFollowing_IdAndFollower_Id(userId, viewerId);
        boolean friend = me || (following && userFollowRepository.existsByFollowing_IdAndFollower_Id(viewerId, userId));

        return new UserDto.ProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getAvatar(),
                user.getRegisterTime(),
                userFollowRepository.countFollowersWithoutFriends(userId),
                userFollowRepository.countFollowingWithoutFriends(userId),
                userFollowRepository.countFriends(userId),
                me,
                following,
                friend
        );
    }

    private User getUserById(Long userId) {
        return userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
    }

    private void requireActiveUser(Long userId) {
        if (!userRepository.existsByIdAndActiveTrue(userId)) {
            throw new RuntimeException("USER_NOT_FOUND");
        }
    }

    private Long extractUserId(String token) {
        String cleanToken = jwtService.normalizeBearerToken(token);
        return jwtService.extractUserId(cleanToken);
    }
}
