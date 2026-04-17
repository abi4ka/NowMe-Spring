package com.abik.nowme.module.nowme.service;

import com.abik.nowme.module.nowme.entity.Nowme;
import com.abik.nowme.module.nowme.entity.NowmeLike;
import com.abik.nowme.module.nowme.repository.NowmeLikeRepository;
import com.abik.nowme.module.nowme.repository.NowmeRepository;
import com.abik.nowme.module.shared.service.JwtService;
import com.abik.nowme.module.user.entity.User;
import com.abik.nowme.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final NowmeRepository nowmeRepository;
    private final NowmeLikeRepository likeRepository;

    public Long like(String token, Long nowmeId) {
        String cleanToken = jwtService.normalizeBearerToken(token);
        Long userId = jwtService.extractUserId(cleanToken);

        User user = userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        Nowme nowme = nowmeRepository.findById(nowmeId)
                .orElseThrow(() -> new RuntimeException("NOWME_NOT_FOUND"));

        if (!nowme.getUser().isActive()) {
            throw new RuntimeException("NOWME_NOT_FOUND");
        }

        var existingLike = likeRepository.findByUserIdAndNowmeId(userId, nowmeId);

        if (existingLike.isPresent()) {
            throw new RuntimeException("ALREADY_LIKED");
        }

        NowmeLike like = NowmeLike.builder()
                .user(user)
                .nowme(nowme)
                .build();

        likeRepository.save(like);

        Long likesCount = likeRepository.countByNowmeId(nowmeId);

        nowmeRepository.save(nowme);

        return likesCount;
    }

    public Long unlike(String token, Long nowmeId) {
        String cleanToken = jwtService.normalizeBearerToken(token);
        Long userId = jwtService.extractUserId(cleanToken);

        if (!userRepository.existsByIdAndActiveTrue(userId)) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

        NowmeLike like = likeRepository.findByUserIdAndNowmeId(userId, nowmeId)
                .orElseThrow(() -> new RuntimeException("LIKE_NOT_FOUND"));

        likeRepository.delete(like);
        Long likesCount = likeRepository.countByNowmeId(nowmeId);

        Nowme nowme = nowmeRepository.findById(nowmeId)
                .orElseThrow(() -> new RuntimeException("NOWME_NOT_FOUND"));

        if (!nowme.getUser().isActive()) {
            throw new RuntimeException("NOWME_NOT_FOUND");
        }

        nowmeRepository.save(nowme);

        return likesCount;
    }
}
