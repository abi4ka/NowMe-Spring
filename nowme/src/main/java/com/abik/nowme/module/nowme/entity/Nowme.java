package com.abik.nowme.module.nowme.entity;


import com.abik.nowme.module.user.Visibility;
import com.abik.nowme.module.user.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"nowme\"")
public class Nowme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creationTime= LocalDateTime.now();;

    @Column(nullable = false)
    private Visibility visibility = Visibility.PUBLIC;

    @Column(nullable = false)
    private Boolean favorite = false;

    @Column(nullable = false)
    private String image;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private boolean active = true;
}
