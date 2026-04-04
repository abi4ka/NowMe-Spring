package com.abik.nowme.module.user.service;

import com.abik.nowme.module.shared.service.JwtService;
import com.abik.nowme.module.user.entity.User;
import com.abik.nowme.module.user.entity.Follow;
import com.abik.nowme.module.user.repository.UserFollowRepository;
import com.abik.nowme.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;
    private final UserFollowRepository followRepository;
    private final JwtService jwtService;

    public void follow(String token, Long userIdToFollow) {

        String username = jwtService.extractUsername(token);

        User follower = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User user = userRepository.findById(userIdToFollow)
                .orElseThrow(() -> new RuntimeException("User to follow not found"));

        if (follower.getId().equals(user.getId())) {
            throw new RuntimeException("You can not follow yourself");
        }

        if (followRepository.existsByUser_IdAndFollower_Id(user.getId(), follower.getId())) {
            throw new RuntimeException("You are already following");
        }

        Follow follow = new Follow();
        follow.setFollowing(user);
        follow.setFollower(follower);

        followRepository.save(follow);


        userRepository.save(user);
        userRepository.save(follower);
    }
}