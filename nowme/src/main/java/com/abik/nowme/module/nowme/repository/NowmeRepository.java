package com.abik.nowme.module.nowme.repository;

import com.abik.nowme.module.nowme.entity.Nowme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NowmeRepository extends JpaRepository<Nowme, Long> {

    List<Nowme> findByUser_IdInOrderByCreationTimeDesc(List<Long> userIds);
}
