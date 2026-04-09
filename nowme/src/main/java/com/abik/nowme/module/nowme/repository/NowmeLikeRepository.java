package com.abik.nowme.module.nowme.repository;

import com.abik.nowme.module.nowme.entity.NowmeLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NowmeLikeRepository extends JpaRepository<NowmeLike, Long> {

    Optional<NowmeLike> findByUserIdAndNowmeId(Long userId, Long nowmeId);

    Long countByNowmeId(Long nowmeId);

    @Query("SELECT nl.nowme.id, COUNT(nl) FROM NowmeLike nl WHERE nl.nowme.id IN :nowmeIds GROUP BY nl.nowme.id")
    List<Object[]> countByNowmeIdIn(@Param("nowmeIds") List<Long> nowmeIds);
}