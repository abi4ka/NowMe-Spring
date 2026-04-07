package com.abik.nowme.module.nowme.repository;

import com.abik.nowme.module.nowme.entity.NowmeLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NowmeLikeRepository extends JpaRepository<NowmeLike, Long> {

    Optional<NowmeLike> findByUserIdAndNowmeId(Long userId, Long nowmeId);

    Long countByNowmeId(Long nowmeId);
}