package com.abik.nowme.module.user.repository;

import com.abik.nowme.module.user.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

    boolean existsByUser_IdAndFollower_Id(Long userId, Long followerId);

    void deleteByUserIdAndFollowerId(Long userId, Long followerId);
}