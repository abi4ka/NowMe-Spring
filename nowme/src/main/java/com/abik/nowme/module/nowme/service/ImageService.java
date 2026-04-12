package com.abik.nowme.module.nowme.service;

import com.abik.nowme.module.nowme.entity.Nowme;
import com.abik.nowme.module.nowme.repository.NowmeRepository;
import com.abik.nowme.module.shared.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final Path uploadDir = Paths.get("uploads");
    private final NowmeRepository nowmeRepository;
    private final JwtService jwtService;
    private final NowmeImageAccessService nowmeImageAccessService;

    public Resource getNowmeImageById(String token, Long id) {
        String cleanToken = jwtService.normalizeBearerToken(token);
        Long userId = jwtService.extractUserId(cleanToken);

        Nowme nowme = nowmeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOWME_NOT_FOUND"));

        if (!nowmeImageAccessService.hasAccess(userId, nowme)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "IMAGE_ACCESS_DENIED");
        }

        String filename = nowme.getImage();
        Path path = uploadDir.resolve(filename);

        try {
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("FILE_NOT_FOUND");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("FILE_NOT_FOUND", e);
        }
    }
}
