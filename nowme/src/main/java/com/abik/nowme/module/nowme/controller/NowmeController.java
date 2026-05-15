package com.abik.nowme.module.nowme.controller;

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
            @RequestHeader("Authorization") String token,
            @RequestPart("image") MultipartFile image,
            @RequestPart(value = "description", required = false) String description) {

        return nowmeService.createNowme(token, image, description);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getImage(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        Resource resource = imageService.getNowmeImageById(token, id);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @GetMapping
    public Page<NowmeResponse> getNowmes(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return nowmeService.getUserNowmesLast7Days(token, page, size);
    }

    @GetMapping("/me/history")
    public List<NowmeResponse> getMyNowmeHistory(
            @RequestHeader("Authorization") String token) {

        return nowmeService.getMyNowmeHistory(token);
    }

    @GetMapping("/users/{userId}")
    public List<NowmeResponse> getProfileNowmes(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {

        return nowmeService.getProfileNowmes(token, userId);
    }

    @PutMapping("/{id}/visibility")
    public ResponseEntity<NowmeResponse> updateVisibility(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Valid @RequestBody UpdateNowmeVisibilityRequest request) {
        NowmeResponse response = nowmeService.updateVisibility(token, id, request.visibility());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/favorite")
    public ResponseEntity<NowmeResponse> toggleFavorite(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        NowmeResponse response = nowmeService.toggleFavorite(token, id);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNowme(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        nowmeService.deleteNowme(token, id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Long> like(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        Long likes = likeService.like(token, id);

        return ResponseEntity.ok(likes);
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Long> unlike(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        Long likes = likeService.unlike(token, id);

        return ResponseEntity.ok(likes);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<CommentResponse> comment(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request) {
        CommentResponse response = commentService.createComment(token, id, request.content());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/comment")
    public Page<CommentResponse> getComments(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return commentService.getComments(token, id, page, size);
    }
}
