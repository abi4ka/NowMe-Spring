package com.abik.nowme.module.nowme.repository;

import com.abik.nowme.module.nowme.entity.Nowme;
import com.abik.nowme.module.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface NowmeRepository extends JpaRepository<Nowme, Long> {

    Page<Nowme> findByUserAndCreationTimeAfterOrderByCreationTimeDesc(
            User user,
            LocalDateTime date,
            Pageable pageable
    );
}