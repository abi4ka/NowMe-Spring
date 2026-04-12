package com.abik.nowme.module.user.service;

import com.abik.nowme.module.user.repository.UserFollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final UserFollowRepository userFollowRepository;

    public boolean areFriends(Long firstUserId, Long secondUserId) {
        if (firstUserId == null || secondUserId == null) {
            return false;
        }

        if (firstUserId.equals(secondUserId)) {
            return true;
        }

        return userFollowRepository.existsByFollowing_IdAndFollower_Id(firstUserId, secondUserId)
                && userFollowRepository.existsByFollowing_IdAndFollower_Id(secondUserId, firstUserId);
    }
}
