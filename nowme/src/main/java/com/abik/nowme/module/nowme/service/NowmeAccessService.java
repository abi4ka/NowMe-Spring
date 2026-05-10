package com.abik.nowme.module.nowme.service;

import com.abik.nowme.module.nowme.entity.Nowme;
import com.abik.nowme.module.nowme.repository.NowmeRepository;
import com.abik.nowme.module.user.Visibility;
import com.abik.nowme.module.user.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NowmeAccessService {

    private final NowmeRepository nowmeRepository;
    private final FriendshipService friendshipService;

    public boolean hasAccess(Long userId, Long nowmeId) {
        Nowme nowme = nowmeRepository.findByIdAndActiveTrue(nowmeId)
                .orElseThrow(() -> new RuntimeException("NOWME_NOT_FOUND"));

        return hasAccess(userId, nowme);
    }

    public boolean hasAccess(Long userId, Nowme nowme) {
        return hasAccess(userId, nowme, true);
    }

    public boolean hasFeedAccess(Long userId, Nowme nowme) {
        return hasAccess(userId, nowme, false);
    }

    private boolean hasAccess(Long userId, Nowme nowme, boolean allowFavorite) {
        if (!nowme.isActive() || !nowme.getUser().isActive()) {
            return false;
        }

        Long authorId = nowme.getUser().getId();
        Visibility visibility = nowme.getVisibility();
        boolean isFavorite = Boolean.TRUE.equals(nowme.getFavorite());
        boolean isFresh = !nowme.getCreationTime().isBefore(LocalDateTime.now().minusDays(7));

        if (authorId.equals(userId)) {
            return allowFavorite || isFresh;
        }

        if (visibility == Visibility.PUBLIC) {
            return isFresh || (allowFavorite && isFavorite);
        }

        boolean isFriend = friendshipService.areFriends(userId, authorId);

        if (isFriend && visibility == Visibility.FRIENDS_ONLY) {
            return isFresh || (allowFavorite && isFavorite);
        }

        return false;
    }
}
