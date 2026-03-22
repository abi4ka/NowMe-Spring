package com.abik.nowme.module.nowme.service;

import com.abik.nowme.module.nowme.entity.Nowme;
import com.abik.nowme.module.nowme.repository.NowmeRepository;
import com.abik.nowme.module.shared.service.JwtService;
import com.abik.nowme.module.user.entity.User;
import com.abik.nowme.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NowmeService {

    private final NowmeRepository nowmeRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public Long createNowme(String token, MultipartFile image, String description) {
        String username = jwtService.extractUsername(token);

        User user = userRepository.findByUsername(username)
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
}