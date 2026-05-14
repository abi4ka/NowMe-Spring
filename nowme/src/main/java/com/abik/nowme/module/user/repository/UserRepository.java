package com.abik.nowme.module.user.repository;

import com.abik.nowme.module.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndActiveTrue(String username);

    Optional<User> findByIdAndActiveTrue(Long id);

    boolean existsByIdAndActiveTrue(Long id);

    Page<User> findByUsernameContainingIgnoreCaseAndActiveTrue(String username, Pageable pageable);
}
