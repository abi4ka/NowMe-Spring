package com.abik.nowme.module.user.repository;

import com.abik.nowme.module.user.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByUser_IdAndFollower_Id(Long userId, Long followerId);

    void deleteByUserIdAndFollowerId(Long userId, Long followerId);
}