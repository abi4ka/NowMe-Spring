package com.abik.nowme.module.nowme.service;

import com.abik.nowme.module.nowme.dto.CommentResponse;
import com.abik.nowme.module.nowme.entity.Comment;
import com.abik.nowme.module.nowme.repository.CommentRepository;
import com.abik.nowme.module.nowme.entity.Nowme;
import com.abik.nowme.module.nowme.repository.NowmeRepository;
import com.abik.nowme.module.shared.service.JwtService;
import com.abik.nowme.module.user.entity.User;
import com.abik.nowme.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NowmeRepository nowmeRepository;
    private final JwtService jwtService;
    private final NowmeAccessService nowmeAccessService;


    public CommentResponse createComment(String token, Long nowmeId, String content) {
        Long userId = jwtService.getUserIdFromToken(token);

        User user = userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        Nowme nowme = nowmeRepository.findByIdAndActiveTrue(nowmeId)
                .orElseThrow(() -> new RuntimeException("NOWME_NOT_FOUND"));

        if (!nowme.getUser().isActive()) {
            throw new RuntimeException("NOWME_NOT_FOUND");
        }

        if (!nowmeAccessService.hasFeedAccess(userId, nowme)) {
            throw new RuntimeException("ACCESS_DENIED");
        }

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setNowme(nowme);
        comment.setContent(content);

        commentRepository.save(comment);

        return new CommentResponse(
                comment.getId(),
                user.getId(),
                user.getAvatar(),
                user.getUsername(),
                comment.getContent()
        );
    }

    public Page<CommentResponse> getComments(String token, Long nowmeId, int page, int size) {
        Long userId = jwtService.getUserIdFromToken(token);

        if (!userRepository.existsByIdAndActiveTrue(userId)) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

        Nowme nowme = nowmeRepository.findByIdAndActiveTrue(nowmeId)
                .orElseThrow(() -> new RuntimeException("NOWME_NOT_FOUND"));

        if (!nowme.getUser().isActive()) {
            throw new RuntimeException("NOWME_NOT_FOUND");
        }

        if (!nowmeAccessService.hasFeedAccess(userId, nowme)) {
            throw new RuntimeException("ACCESS_DENIED");
        }

        PageRequest pageable = PageRequest.of(page, size);

        return commentRepository
                .findByNowmeIdAndActiveTrueOrderByCreatedAtDesc(nowmeId, pageable)
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getUser().getId(),
                        comment.getUser().getAvatar(),
                        comment.getUser().getUsername(),
                        comment.getContent()
                ));
    }
}
