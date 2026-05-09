package com.abik.nowme.module.nowme.repository;

import com.abik.nowme.module.nowme.entity.Nowme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NowmeRepository extends JpaRepository<Nowme, Long> {

    List<Nowme> findByUser_IdAndActiveTrueOrderByCreationTimeDesc(Long userId);

    Optional<Nowme> findByIdAndActiveTrue(Long id);

    @Query("""
            select n
            from Nowme n
            where n.active = true
              and n.user.active = true
              and n.user.id in :userIds
            order by n.creationTime desc
            """)
    List<Nowme> findActiveByActiveUserIdInOrderByCreationTimeDesc(List<Long> userIds);
}
