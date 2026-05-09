package com.abik.nowme.module.user.service;

import com.abik.nowme.module.nowme.entity.Nowme;
import com.abik.nowme.module.nowme.repository.NowmeRepository;
import com.abik.nowme.module.shared.service.JwtService;
import com.abik.nowme.module.user.dto.UserProfileResponse;
import com.abik.nowme.module.user.dto.UserSearchResponse;
import com.abik.nowme.module.user.entity.User;
import com.abik.nowme.module.user.repository.UserFollowRepository;
import com.abik.nowme.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;
    private final NowmeRepository nowmeRepository;
    private final JwtService jwtService;

    public UserProfileResponse getMyProfile(String token) {
        Long userId = extractUserId(token);
        User user = getUserById(userId);

        return toProfileResponse(user, userId);
    }

    public UserProfileResponse getProfileById(String token, Long userId) {
        Long viewerId = extractUserId(token);
        requireActiveUser(viewerId);
        User user = getUserById(userId);

        return toProfileResponse(user, viewerId);
    }

    public UserProfileResponse getProfileByUsername(String token, String username) {
        Long viewerId = extractUserId(token);
        requireActiveUser(viewerId);
        User user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        return toProfileResponse(user, viewerId);
    }

    private UserProfileResponse toProfileResponse(User user, Long viewerId) {
        Long userId = user.getId();
        boolean me = userId.equals(viewerId);
        boolean following = !me && userFollowRepository.existsByFollowing_IdAndFollower_Id(userId, viewerId);
        boolean friend = me || (following && userFollowRepository.existsByFollowing_IdAndFollower_Id(viewerId, userId));

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getAvatar(),
                user.getRegisterTime(),
                userFollowRepository.countFollowersWithoutFriends(userId),
                userFollowRepository.countFollowingWithoutFriends(userId),
                userFollowRepository.countFriends(userId),
                calculateStreakDays(userId),
                me,
                following,
                friend
        );
    }

    public List<UserSearchResponse> searchUsers(String token, String query) {

        Long userId = extractUserId(token);
        requireActiveUser(userId);

        List<User> users = userRepository.findByUsernameContainingIgnoreCaseAndActiveTrue(query);

        return users.stream()
                .map(user -> new UserSearchResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getAvatar()
                ))
                .toList();
    }

    private long calculateStreakDays(Long userId) {
        List<Nowme> nowmes = nowmeRepository.findActiveByActiveUserIdInOrderByCreationTimeDesc(List.of(userId));
        if (nowmes.isEmpty()) {
            return 0L;
        }

        Set<LocalDate> postDates = new TreeSet<>((first, second) -> second.compareTo(first));
        for (Nowme nowme : nowmes) {
            postDates.add(nowme.getCreationTime().toLocalDate());
        }

        LocalDate previousPostDate = postDates.iterator().next();
        if (LocalDate.now().toEpochDay() - previousPostDate.toEpochDay() > 2L) {
            return 0L;
        }

        long streakDays = 1L;
        boolean skipFirst = true;
        for (LocalDate postDate : postDates) {
            if (skipFirst) {
                skipFirst = false;
                continue;
            }

            long gap = previousPostDate.toEpochDay() - postDate.toEpochDay();

            if (gap > 2L) {
                break;
            }

            streakDays++;
            previousPostDate = postDate;
        }

        return streakDays;
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

    public void updateAvatar(String token, String avatar) {
        Long userId = jwtService.extractUserId(jwtService.normalizeBearerToken(token));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        user.setAvatar(avatar);
        userRepository.save(user);
    }
}
