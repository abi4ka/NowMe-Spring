package com.abik.nowme.module.user.repository;

import com.abik.nowme.module.user.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowing_IdAndFollower_Id(Long followingId, Long followerId);

    void deleteByFollowing_IdAndFollower_Id(Long followingId, Long followerId);
}