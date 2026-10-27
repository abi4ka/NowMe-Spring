package com.abik.nowme.module.nowme.service;

import com.abik.nowme.module.nowme.dto.NowmeResponse;
import com.abik.nowme.module.nowme.entity.Nowme;
import com.abik.nowme.module.nowme.repository.CommentRepository;
import com.abik.nowme.module.nowme.repository.NowmeLikeRepository;
import com.abik.nowme.module.nowme.repository.NowmeRepository;
import com.abik.nowme.module.shared.service.Paging;
import com.abik.nowme.module.shared.service.JwtService;
import com.abik.nowme.module.user.Visibility;
import com.abik.nowme.module.user.entity.User;
import com.abik.nowme.module.user.repository.UserFollowRepository;
import com.abik.nowme.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NowmeService {

    public static final int VISIBLE_POST_DAYS = 7;

    private final NowmeRepository nowmeRepository;
    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;
    private final NowmeLikeRepository nowmeLikeRepository;
    private final CommentRepository commentRepository;
    private final NowmeAccessService nowmeAccessService;

    public Long createNowme(Long userId, MultipartFile image, String description) {
        User user = userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        String originalName = image.getOriginalFilename() != null ? Paths.get(image.getOriginalFilename()).getFileName().toString() : "upload.jpg";
        String fileName = UUID.randomUUID() + "_" + originalName;
        Path path = Paths.get("uploads/" + fileName);

        try {
            Files.createDirectories(path.getParent());
            Files.write(path, image.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("FAILED_TO_SAVE_IMAGE", e);
        }

        Nowme nowme = new Nowme();
        nowme.setUser(user);
        nowme.setImage(fileName);
        nowme.setDescription(description);

        nowmeRepository.save(nowme);

        return nowme.getId();
    }

    public Page<NowmeResponse> getUserNowmesLast7Days(Long userId, int page, int size) {
        if (!userRepository.existsByIdAndActiveTrue(userId)) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

        List<Long> authorIds = Stream.concat(
                        Stream.of(userId),
                        userFollowRepository.findFollowingIdsByFollowerId(userId).stream()
                )
                .distinct()
                .toList();

        List<Nowme> availableNowmes = nowmeRepository.findActiveByActiveUserIdInSinceOrderByCreationTimeDesc(
                        authorIds,
                        getVisiblePostsSince()
                ).stream()
                .filter(nowme -> nowmeAccessService.hasFeedAccess(userId, nowme))
                .toList();

        Pageable pageable = Paging.of(page, size);
        long offset = pageable.getOffset();

        List<Nowme> pageContent = offset >= availableNowmes.size()
                ? Collections.emptyList()
                : availableNowmes.subList((int) offset,
                        (int) Math.min(offset + pageable.getPageSize(), availableNowmes.size()));

        List<Long> nowmeIds = pageContent.stream()
                .map(Nowme::getId)
                .toList();

        Map<Long, Long> likeCounts = nowmeIds.isEmpty()
                ? Collections.emptyMap()
                : nowmeLikeRepository.countByNowmeIdIn(nowmeIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        Map<Long, Long> commentCounts = nowmeIds.isEmpty()
                ? Collections.emptyMap()
                : commentRepository.countByNowmeIdIn(nowmeIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        List<Long> likedIds = nowmeIds.isEmpty()
                ? Collections.emptyList()
                : nowmeLikeRepository.findLikedNowmeIds(userId, nowmeIds);

        Map<Long, Boolean> likedMap = likedIds.stream()
                .collect(Collectors.toMap(id -> id, id -> true));

        List<NowmeResponse> content = mapToDto(pageContent, likeCounts, commentCounts, likedMap, userId);

        return new PageImpl<>(content, pageable, availableNowmes.size());
    }

    public List<NowmeResponse> getProfileNowmes(Long userId, Long profileUserId) {
        if (!userRepository.existsByIdAndActiveTrue(userId)) {
            throw new RuntimeException("USER_NOT_FOUND");
        }
        if (!userRepository.existsByIdAndActiveTrue(profileUserId)) {
            throw new RuntimeException("PROFILE_USER_NOT_FOUND");
        }

        List<Nowme> profileNowmes = nowmeRepository.findActiveByUserIdVisibleOnProfileOrderByCreationTimeDesc(
                        profileUserId,
                        getVisiblePostsSince()
                ).stream()
                .filter(nowme -> nowmeAccessService.hasProfileAccess(userId, nowme))
                .toList();

        List<Long> nowmeIds = profileNowmes.stream()
                .map(Nowme::getId)
                .toList();

        Map<Long, Long> likeCounts = nowmeIds.isEmpty()
                ? Collections.emptyMap()
                : nowmeLikeRepository.countByNowmeIdIn(nowmeIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        Map<Long, Long> commentCounts = nowmeIds.isEmpty()
                ? Collections.emptyMap()
                : commentRepository.countByNowmeIdIn(nowmeIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        List<Long> likedIds = nowmeIds.isEmpty()
                ? Collections.emptyList()
                : nowmeLikeRepository.findLikedNowmeIds(userId, nowmeIds);

        Map<Long, Boolean> likedMap = likedIds.stream()
                .collect(Collectors.toMap(id -> id, id -> true));

        return mapToDto(profileNowmes, likeCounts, commentCounts, likedMap, userId);
    }

    public List<NowmeResponse> getMyNowmeHistory(Long userId) {
        if (!userRepository.existsByIdAndActiveTrue(userId)) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

        List<Nowme> historyNowmes = nowmeRepository.findByUser_IdAndActiveTrueOrderByCreationTimeDesc(userId);

        List<Long> nowmeIds = historyNowmes.stream()
                .map(Nowme::getId)
                .toList();

        Map<Long, Long> likeCounts = nowmeIds.isEmpty()
                ? Collections.emptyMap()
                : nowmeLikeRepository.countByNowmeIdIn(nowmeIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        Map<Long, Long> commentCounts = nowmeIds.isEmpty()
                ? Collections.emptyMap()
                : commentRepository.countByNowmeIdIn(nowmeIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        List<Long> likedIds = nowmeIds.isEmpty()
                ? Collections.emptyList()
                : nowmeLikeRepository.findLikedNowmeIds(userId, nowmeIds);

        Map<Long, Boolean> likedMap = likedIds.stream()
                .collect(Collectors.toMap(id -> id, id -> true));

        return mapToDto(historyNowmes, likeCounts, commentCounts, likedMap, userId);
    }

    public NowmeResponse updateVisibility(Long userId, Long nowmeId, Visibility visibility) {
        Nowme nowme = findOwnedNowme(userId, nowmeId);

        nowme.setVisibility(visibility);
        nowmeRepository.save(nowme);

        return mapSingleNowme(nowme, userId);
    }

    public NowmeResponse toggleFavorite(Long userId, Long nowmeId) {
        Nowme nowme = findOwnedNowme(userId, nowmeId);
        nowme.setFavorite(!Boolean.TRUE.equals(nowme.getFavorite()));
        nowmeRepository.save(nowme);

        return mapSingleNowme(nowme, userId);
    }

    public void deleteNowme(Long userId, Long nowmeId) {
        Nowme nowme = findOwnedNowme(userId, nowmeId);

        nowme.setActive(false);
        nowmeRepository.save(nowme);
    }

    private Nowme findOwnedNowme(Long userId, Long nowmeId) {
        Nowme nowme = nowmeRepository.findByIdAndActiveTrue(nowmeId)
                .orElseThrow(() -> new RuntimeException("NOWME_NOT_FOUND"));
        if (!nowme.getUser().isActive()) {
            throw new RuntimeException("USER_NOT_FOUND");
        }
        if (!nowme.getUser().getId().equals(userId)) {
            throw new RuntimeException("NOWME_ACCESS_DENIED");
        }

        return nowme;
    }

    private LocalDateTime getVisiblePostsSince() {
        return LocalDateTime.now().minusDays(VISIBLE_POST_DAYS);
    }

    private NowmeResponse mapSingleNowme(Nowme nowme, Long userId) {
        Long nowmeId = nowme.getId();
        long likes = nowmeLikeRepository.countByNowmeId(nowmeId);
        long comments = commentRepository.countByNowmeIdAndActiveTrue(nowmeId);
        boolean liked = nowmeLikeRepository.findByUserIdAndNowmeId(userId, nowmeId).isPresent();

        return mapToDto(
                List.of(nowme),
                Map.of(nowmeId, likes),
                Map.of(nowmeId, comments),
                Map.of(nowmeId, liked),
                userId
        ).get(0);
    }

    private List<NowmeResponse> mapToDto(
            List<Nowme> nowmes,
            Map<Long, Long> likeCounts,
            Map<Long, Long> commentCounts,
            Map<Long, Boolean> likedMap,
            Long viewerId
    ) {
        return nowmes.stream()
                .map(nowme -> new NowmeResponse(
                        nowme.getId(),
                        nowme.getUser().getId(),
                        nowme.getDescription(),
                        nowme.getCreationTime(),
                        nowme.getVisibility(),
                        likeCounts.getOrDefault(nowme.getId(), 0L),
                        commentCounts.getOrDefault(nowme.getId(), 0L),
                        nowme.getUser().getUsername(),
                        nowme.getUser().getAvatar(),
                        nowme.getUser().getId().equals(viewerId),
                        nowme.getFavorite(),
                        likedMap.getOrDefault(nowme.getId(), false)
                ))
                .toList();
    }
}
