package com.abik.nowme.service;

import com.abik.nowme.module.nowme.entity.Nowme;
import com.abik.nowme.module.nowme.entity.NowmeLike;
import com.abik.nowme.module.nowme.repository.NowmeLikeRepository;
import com.abik.nowme.module.nowme.repository.NowmeRepository;
import com.abik.nowme.module.nowme.service.LikeService;
import com.abik.nowme.module.nowme.service.NowmeAccessService;
import com.abik.nowme.module.user.entity.User;
import com.abik.nowme.module.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NowmeRepository nowmeRepository;

    @Mock
    private NowmeLikeRepository likeRepository;

    @Mock
    private NowmeAccessService nowmeAccessService;

    private LikeService likeService;

    @BeforeEach
    void setUp() {
        likeService = new LikeService(userRepository, nowmeRepository, likeRepository, nowmeAccessService);
    }

    @Test
    void like_Success() {
        Long userId = 1L;
        Long nowmeId = 10L;

        User user = new User();
        user.setId(userId);
        user.setActive(true);

        User author = new User();
        author.setId(2L);
        author.setActive(true);

        Nowme nowme = new Nowme();
        nowme.setId(nowmeId);
        nowme.setUser(author);
        nowme.setActive(true);

        when(userRepository.findByIdAndActiveTrue(userId)).thenReturn(Optional.of(user));
        when(nowmeRepository.findByIdAndActiveTrue(nowmeId)).thenReturn(Optional.of(nowme));
        when(nowmeAccessService.hasAccess(userId, nowme)).thenReturn(true);
        when(likeRepository.findByUserIdAndNowmeId(userId, nowmeId)).thenReturn(Optional.empty());
        when(likeRepository.countByNowmeId(nowmeId)).thenReturn(1L);

        Long likesCount = likeService.like(userId, nowmeId);

        assertEquals(1L, likesCount);
        verify(likeRepository, times(1)).save(any(NowmeLike.class));
    }

    @Test
    void like_AlreadyLiked_ThrowsException() {
        Long userId = 1L;
        Long nowmeId = 10L;

        User user = new User();
        user.setId(userId);

        User author = new User();
        author.setId(2L);
        author.setActive(true);

        Nowme nowme = new Nowme();
        nowme.setId(nowmeId);
        nowme.setUser(author);
        nowme.setActive(true);

        when(userRepository.findByIdAndActiveTrue(userId)).thenReturn(Optional.of(user));
        when(nowmeRepository.findByIdAndActiveTrue(nowmeId)).thenReturn(Optional.of(nowme));
        when(nowmeAccessService.hasAccess(userId, nowme)).thenReturn(true);
        when(likeRepository.findByUserIdAndNowmeId(userId, nowmeId)).thenReturn(Optional.of(new NowmeLike()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> likeService.like(userId, nowmeId));
        assertEquals("ALREADY_LIKED", exception.getMessage());
        verify(likeRepository, never()).save(any(NowmeLike.class));
    }

    @Test
    void unlike_Success() {
        Long userId = 1L;
        Long nowmeId = 10L;
        NowmeLike existingLike = new NowmeLike();

        when(userRepository.existsByIdAndActiveTrue(userId)).thenReturn(true);
        when(likeRepository.findByUserIdAndNowmeId(userId, nowmeId)).thenReturn(Optional.of(existingLike));
        when(likeRepository.countByNowmeId(nowmeId)).thenReturn(0L);

        Long likesCount = likeService.unlike(userId, nowmeId);

        assertEquals(0L, likesCount);
        verify(likeRepository, times(1)).delete(existingLike);
    }

    @Test
    void unlike_NotLiked_ThrowsLikeNotFound() {
        Long userId = 1L;
        Long nowmeId = 10L;

        when(userRepository.existsByIdAndActiveTrue(userId)).thenReturn(true);
        when(likeRepository.findByUserIdAndNowmeId(userId, nowmeId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> likeService.unlike(userId, nowmeId));
        assertEquals("LIKE_NOT_FOUND", exception.getMessage());
    }
}
