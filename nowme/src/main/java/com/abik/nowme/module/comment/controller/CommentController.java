package com.abik.nowme.module.comment.controller;

import com.abik.nowme.module.comment.dto.CommentDto;
import com.abik.nowme.module.comment.dto.CommentRequest;
import com.abik.nowme.module.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nowme")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{id}/comment")
    public ResponseEntity<CommentDto> comment(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody CommentRequest request
    ) {

        CommentDto response = commentService.createComment(
                token,
                id,
                request.content()
        );

        return ResponseEntity.ok(response);
    }
}