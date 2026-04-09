package com.abik.nowme.module.nowme.repository;

import com.abik.nowme.module.nowme.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByNowmeId(Long nowmeId);
}