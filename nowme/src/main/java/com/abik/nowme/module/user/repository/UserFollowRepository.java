package com.abik.nowme.module.user.repository;

import com.abik.nowme.module.user.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserFollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowing_IdAndFollower_Id(Long followingId, Long followerId);

    @Query("select f.following.id from Follow f where f.follower.id = :followerId")
    List<Long> findFollowingIdsByFollowerId(Long followerId);

    long countByFollowing_Id(Long followingId);

    long countByFollower_Id(Long followerId);

    @Query("""
            select count(f)
            from Follow f
            where f.following.id = :userId
              and f.follower.active = true
              and not exists (
                  select 1
                  from Follow reverse
                  where reverse.follower.id = :userId
                    and reverse.following.id = f.follower.id
              )
            """)
    long countFollowersWithoutFriends(Long userId);

    @Query("""
            select count(f)
            from Follow f
            where f.follower.id = :userId
              and f.following.active = true
              and not exists (
                  select 1
                  from Follow reverse
                  where reverse.following.id = :userId
                    and reverse.follower.id = f.following.id
              )
            """)
    long countFollowingWithoutFriends(Long userId);

    @Query("""
            select count(f)
            from Follow f
            where f.follower.id = :userId
              and f.following.active = true
              and exists (
                  select 1
                  from Follow reverse
                  where reverse.following.id = :userId
                    and reverse.follower.id = f.following.id
              )
            """)
    long countFriends(Long userId);

    void deleteByFollowing_IdAndFollower_Id(Long followingId, Long followerId);
}
