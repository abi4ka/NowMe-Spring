package com.abik.nowme.module.user.repository;

import com.abik.nowme.module.user.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserFollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowing_IdAndFollower_Id(Long followingId, Long followerId);

    @Query("select f.following.id from Follow f where f.follower.id = :followerId")
    List<Long> findFollowingIdsByFollowerId(Long followerId);

    void deleteByFollowing_IdAndFollower_Id(Long followingId, Long followerId);
}
