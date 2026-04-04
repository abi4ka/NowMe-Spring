package com.abik.nowme.module.nowme.controller;

import com.abik.nowme.module.nowme.dto.NowmeDTO;
import com.abik.nowme.module.nowme.service.ImageService;
import com.abik.nowme.module.nowme.service.NowmeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
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

        return nowmeService.createNowme(token, image, description);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getImage(@PathVariable Long id) {
        Resource resource = imageService.getNowmeImageById(id);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @GetMapping
    public Page<NowmeDTO> getNowmes(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return nowmeService.getUserNowmesLast7Days(token, page, size);
    }
}
