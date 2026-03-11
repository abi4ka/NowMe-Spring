package com.abik.nowme.module.user.entity;

import com.abik.nowme.module.user.Visibility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    String avatar = "🗽";

    @Column(nullable = false)
    private Long level = 1L;

    @Column(nullable = false)
    private Long experience = 0L;

    @Column(nullable = false)
    Visibility visibility = Visibility.PUBLIC;

    @Column(nullable = false)
    private LocalDateTime registerTime = LocalDateTime.now();

    @Column(nullable = false)
    private boolean active = true;
}
