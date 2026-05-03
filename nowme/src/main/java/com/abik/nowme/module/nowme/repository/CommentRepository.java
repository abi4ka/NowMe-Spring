package com.abik.nowme.module.nowme.repository;

import com.abik.nowme.module.nowme.entity.Comment;
import com.abik.nowme.module.nowme.entity.Nowme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByNowmeId(Long nowmeId);
    
    @Query("SELECT c.nowme.id, COUNT(c) FROM Comment c WHERE c.nowme.id IN :nowmeIds GROUP BY c.nowme.id")
    List<Object[]> countByNowmeIdIn(@Param("nowmeIds") List<Long> nowmeIds);

    Page<Comment> findByNowmeIdAndActiveTrueOrderByCreatedAtDesc(
            Long nowmeId,
            Pageable pageable
    );
}