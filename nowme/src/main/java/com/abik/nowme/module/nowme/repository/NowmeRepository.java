package com.abik.nowme.module.nowme.repository;

import com.abik.nowme.module.nowme.entity.Nowme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NowmeRepository extends JpaRepository<Nowme, Long> {
}