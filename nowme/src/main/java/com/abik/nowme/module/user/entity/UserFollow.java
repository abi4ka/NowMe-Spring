package com.abik.nowme.module.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_follow")
public class UserFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario que es seguido
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Usuario que sigue
    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User follower;
}