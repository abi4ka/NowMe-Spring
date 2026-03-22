package com.abik.nowme.module.nowme.service;

import com.abik.nowme.module.nowme.entity.Nowme;
import com.abik.nowme.module.nowme.repository.NowmeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final Path uploadDir = Paths.get("uploads");
    private final NowmeRepository nowmeRepository;

    public Resource getNowmeImageById(Long id) {
        Nowme nowme = nowmeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOWME_NOT_FOUND"));

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