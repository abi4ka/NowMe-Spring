package com.abik.nowme.module.nowme.repository;

import com.abik.nowme.module.nowme.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByNowmeId(Long nowmeId);

    List<Comment> findByNowmeIdOrderByIdDesc(Long nowmeId);

    @Query("SELECT c.nowme.id, COUNT(c) FROM Comment c WHERE c.nowme.id IN :nowmeIds GROUP BY c.nowme.id")
    List<Object[]> countByNowmeIdIn(@Param("nowmeIds") List<Long> nowmeIds);
}