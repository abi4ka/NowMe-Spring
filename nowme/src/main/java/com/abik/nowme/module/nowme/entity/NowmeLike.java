package com.abik.nowme.module.nowme.entity;

import com.abik.nowme.module.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nowme_like", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "nowme_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NowmeLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "nowme_id")
    private Nowme nowme;
}
