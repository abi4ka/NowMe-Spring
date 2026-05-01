package com.abik.nowme.module.nowme.service;

import com.abik.nowme.module.nowme.dto.CommentDto;
import com.abik.nowme.module.nowme.entity.Comment;
import com.abik.nowme.module.nowme.repository.CommentRepository;
import com.abik.nowme.module.nowme.entity.Nowme;
import com.abik.nowme.module.nowme.repository.NowmeRepository;
import com.abik.nowme.module.shared.service.JwtService;
import com.abik.nowme.module.user.entity.User;
import com.abik.nowme.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NowmeRepository nowmeRepository;
    private final JwtService jwtService;

    public CommentDto createComment(String token, Long nowmeId, String content) {
        String cleanToken = jwtService.normalizeBearerToken(token);
        Long userId = jwtService.extractUserId(cleanToken);

        User user = userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        Nowme nowme = nowmeRepository.findByIdAndActiveTrue(nowmeId)
                .orElseThrow(() -> new RuntimeException("NOWME_NOT_FOUND"));

        if (!nowme.getUser().isActive()) {
            throw new RuntimeException("NOWME_NOT_FOUND");
        }

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setNowme(nowme);
        comment.setContent(content);

        commentRepository.save(comment);

        nowmeRepository.save(nowme);

        return new CommentDto(
                comment.getId(),
                user.getId(),
                user.getAvatar(),
                user.getUsername(),
                comment.getContent()
        );
    }
    public List<CommentDto> getComments(Long nowmeId) {

        Nowme nowme = nowmeRepository.findByIdAndActiveTrue(nowmeId)
                .orElseThrow(() -> new RuntimeException("NOWME_NOT_FOUND"));

        if (!nowme.getUser().isActive()) {
            throw new RuntimeException("NOWME_NOT_FOUND");
        }

        return commentRepository.findByNowmeIdOrderByIdDesc(nowmeId)
                .stream()
                .map(comment -> new CommentDto(
                        comment.getId(),
                        comment.getUser().getId(),
                        comment.getUser().getAvatar(),
                        comment.getUser().getUsername(),
                        comment.getContent()
                ))
                .toList();
    }
}
