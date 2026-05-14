package com.abik.nowme.module.user.entity;

import com.abik.nowme.module.user.Visibility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
    private String avatar = "🗽";

    @Column(nullable = false)
    private Visibility visibility = Visibility.PUBLIC;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registerTime= LocalDateTime.now();;

    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY)
    private List<Follow> following;

    @OneToMany(mappedBy = "following", fetch = FetchType.LAZY)
    private List<Follow> followers;

    @Column(nullable = false)
    private boolean active = true;
}
