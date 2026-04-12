package com.abik.nowme.module.nowme.service;

import com.abik.nowme.module.nowme.dto.NowmeDTO;
import com.abik.nowme.module.nowme.entity.Nowme;
import com.abik.nowme.module.nowme.repository.CommentRepository;
import com.abik.nowme.module.nowme.repository.NowmeLikeRepository;
import com.abik.nowme.module.nowme.repository.NowmeRepository;
import com.abik.nowme.module.shared.service.JwtService;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NowmeService {

    private final NowmeRepository nowmeRepository;
    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;
    private final JwtService jwtService;
    private final NowmeLikeRepository nowmeLikeRepository;
    private final CommentRepository commentRepository;
    private final NowmeImageAccessService nowmeImageAccessService;

    public Long createNowme(String token, MultipartFile image, String description) {
        String cleanToken = jwtService.normalizeBearerToken(token);
        Long userId = jwtService.extractUserId(cleanToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
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

    public Page<NowmeDTO> getUserNowmesLast7Days(String token, int page, int size) {
        String cleanToken = jwtService.normalizeBearerToken(token);
        Long userId = jwtService.extractUserId(cleanToken);

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

        List<Long> authorIds = Stream.concat(
                        Stream.of(userId),
                        userFollowRepository.findFollowingIdsByFollowerId(userId).stream()
                )
                .distinct()
                .toList();

        List<Nowme> availableNowmes = nowmeRepository.findByUser_IdInOrderByCreationTimeDesc(authorIds).stream()
                .filter(nowme -> nowmeImageAccessService.hasFeedAccess(userId, nowme))
                .toList();

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), availableNowmes.size());

        List<Nowme> pageContent = start >= availableNowmes.size()
                ? Collections.emptyList()
                : availableNowmes.subList(start, end);

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

        final Map<Long, Long> likeCountsFinal = likeCounts;
        final Map<Long, Long> commentCountsFinal = commentCounts;

        List<NowmeDTO> content = pageContent.stream()
                .map(nowme -> new NowmeDTO(
                        nowme.getId(),
                        nowme.getDescription(),
                        nowme.getCreationTime(),
                        likeCountsFinal.getOrDefault(nowme.getId(), 0L),
                        commentCountsFinal.getOrDefault(nowme.getId(), 0L),
                        nowme.getUser().getUsername(),
                        nowme.getUser().getAvatar()
                ))
                .toList();

        return new PageImpl<>(content, pageable, availableNowmes.size());
    }
}
