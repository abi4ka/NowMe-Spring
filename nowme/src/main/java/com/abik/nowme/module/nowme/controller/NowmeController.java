package com.abik.nowme.module.nowme.controller;

import com.abik.nowme.module.nowme.service.ImageService;
import com.abik.nowme.module.nowme.service.NowmeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nowme")
public class NowmeController {

    private final NowmeService nowmeService;
    private final ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long createNowme(
            @RequestHeader("Authorization") String token,
            @RequestPart("image") MultipartFile image,
            @RequestPart(value = "description", required = false) String description) {

        Long userId = extractUserIdFromToken(token);

        return nowmeService.createNowme(userId, image, description);
    }

    private Long extractUserIdFromToken(String token) {
        //TODO: JWT token
        return 1L;
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getImage(@PathVariable Long id) {
        Resource resource = imageService.getNowmeImageById(id);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}