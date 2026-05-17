package com.abik.nowme.module.nowme.repository;

import com.abik.nowme.module.nowme.entity.Nowme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NowmeRepository extends JpaRepository<Nowme, Long> {

    List<Nowme> findByUser_IdAndActiveTrueOrderByCreationTimeDesc(Long userId);

    @Query("""
            select n
            from Nowme n
            where n.active = true
              and n.user.active = true
              and n.user.id = :userId
              and (n.creationTime >= :since or n.favorite = true)
            order by n.creationTime desc
            """)
    List<Nowme> findActiveByUserIdVisibleOnProfileOrderByCreationTimeDesc(
            @Param("userId") Long userId,
            @Param("since") LocalDateTime since);

    Optional<Nowme> findByIdAndActiveTrue(Long id);

    @Query("""
            select n
            from Nowme n
            where n.active = true
              and n.user.active = true
              and n.user.id in :userIds
              and n.creationTime >= :since
            order by n.creationTime desc
            """)
    List<Nowme> findActiveByActiveUserIdInSinceOrderByCreationTimeDesc(
            @Param("userIds") List<Long> userIds,
            @Param("since") LocalDateTime since);
}
