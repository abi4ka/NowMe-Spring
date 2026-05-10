package com.abik.nowme.module.user.service;

import com.abik.nowme.module.shared.service.JwtService;
import com.abik.nowme.module.user.entity.User;
import com.abik.nowme.module.user.entity.Follow;
import com.abik.nowme.module.user.repository.UserFollowRepository;
import com.abik.nowme.module.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;
    private final UserFollowRepository followRepository;
    private final JwtService jwtService;

    public void follow(String token, Long userIdToFollow) {
        Long followerId = jwtService.getUserIdFromToken(token);

        User follower = userRepository.findByIdAndActiveTrue(followerId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        User user = userRepository.findByIdAndActiveTrue(userIdToFollow)
                .orElseThrow(() -> new RuntimeException("USER_TO_FOLLOW_NOT_FOUND"));

        if (follower.getId().equals(user.getId())) {
            throw new RuntimeException("CANNOT_FOLLOW_YOURSELF");
        }

        if (followRepository.existsByFollowing_IdAndFollower_Id(user.getId(), follower.getId())) {
            throw new RuntimeException("ALREADY_FOLLOWING");
        }

        Follow follow = new Follow();
        follow.setFollowing(user);
        follow.setFollower(follower);

        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(String token, Long userIdToUnfollow) {
        Long followerId = jwtService.getUserIdFromToken(token);

        User follower = userRepository.findByIdAndActiveTrue(followerId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        User user = userRepository.findByIdAndActiveTrue(userIdToUnfollow)
                .orElseThrow(() -> new RuntimeException("USER_TO_UNFOLLOW_NOT_FOUND"));

        if (!followRepository.existsByFollowing_IdAndFollower_Id(user.getId(), follower.getId())) {
            throw new RuntimeException("YOU_ARE_NOT_FOLLOWING_THIS_USER");
        }

        followRepository.deleteByFollowing_IdAndFollower_Id(user.getId(), follower.getId());
    }
}
