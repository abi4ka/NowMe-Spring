package com.abik.nowme.module.nowme.controller;

import com.abik.nowme.config.CurrentUser;
import com.abik.nowme.module.nowme.dto.CommentResponse;
import com.abik.nowme.module.nowme.dto.CommentRequest;
import com.abik.nowme.module.nowme.dto.NowmeResponse;
import com.abik.nowme.module.nowme.dto.UpdateNowmeVisibilityRequest;
import com.abik.nowme.module.nowme.service.CommentService;
import com.abik.nowme.module.nowme.service.ImageService;
import com.abik.nowme.module.nowme.service.LikeService;
import com.abik.nowme.module.nowme.service.NowmeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nowme")
public class NowmeController {

    private final NowmeService nowmeService;
    private final ImageService imageService;
    private final LikeService likeService;
    private final CommentService commentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long createNowme(
            @CurrentUser Long currentUserId,
            @RequestPart("image") MultipartFile image,
            @RequestPart(value = "description", required = false) String description) {

        return nowmeService.createNowme(currentUserId, image, description);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getImage(
            @CurrentUser Long currentUserId,
            @PathVariable Long id) {
        Resource resource = imageService.getNowmeImageById(currentUserId, id);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @GetMapping
    public Page<NowmeResponse> getNowmes(
            @CurrentUser Long currentUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return nowmeService.getUserNowmesLast7Days(currentUserId, page, size);
    }

    @GetMapping("/me/history")
    public List<NowmeResponse> getMyNowmeHistory(
            @CurrentUser Long currentUserId) {

        return nowmeService.getMyNowmeHistory(currentUserId);
    }

    @GetMapping("/users/{userId}")
    public List<NowmeResponse> getProfileNowmes(
            @CurrentUser Long currentUserId,
            @PathVariable Long userId) {

        return nowmeService.getProfileNowmes(currentUserId, userId);
    }

    @PutMapping("/{id}/visibility")
    public ResponseEntity<NowmeResponse> updateVisibility(
            @CurrentUser Long currentUserId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateNowmeVisibilityRequest request) {
        NowmeResponse response = nowmeService.updateVisibility(currentUserId, id, request.visibility());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/favorite")
    public ResponseEntity<NowmeResponse> toggleFavorite(
            @CurrentUser Long currentUserId,
            @PathVariable Long id) {
        NowmeResponse response = nowmeService.toggleFavorite(currentUserId, id);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNowme(
            @CurrentUser Long currentUserId,
            @PathVariable Long id) {
        nowmeService.deleteNowme(currentUserId, id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Long> like(
            @CurrentUser Long currentUserId,
            @PathVariable Long id) {
        Long likes = likeService.like(currentUserId, id);

        return ResponseEntity.ok(likes);
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Long> unlike(
            @CurrentUser Long currentUserId,
            @PathVariable Long id) {
        Long likes = likeService.unlike(currentUserId, id);

        return ResponseEntity.ok(likes);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<CommentResponse> comment(
            @CurrentUser Long currentUserId,
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request) {
        CommentResponse response = commentService.createComment(currentUserId, id, request.content());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/comment")
    public Page<CommentResponse> getComments(
            @CurrentUser Long currentUserId,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return commentService.getComments(currentUserId, id, page, size);
    }
}
