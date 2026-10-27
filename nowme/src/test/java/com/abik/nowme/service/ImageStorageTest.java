package com.abik.nowme.service;

import com.abik.nowme.module.nowme.service.ImageStorage;
import com.abik.nowme.module.shared.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageStorageTest {

    private final ImageStorage storage = new ImageStorage();

    @Test
    void store_DetectsTypeFromContentNotFilename() {
        byte[] png = {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n', 0, 0, 0, 0};
        String fileName = storage.store(new MockMultipartFile("image", "../../evil.jpg", "image/jpeg", png));
        try {
            assertTrue(fileName.endsWith(".png"));
            assertFalse(fileName.contains("evil"));
            assertTrue(Files.exists(storage.resolve(fileName)));
        } finally {
            storage.delete(fileName);
        }
        assertFalse(Files.exists(storage.resolve(fileName)));
    }

    @Test
    void store_RejectsNonImagesAndEmptyFiles() {
        assertThrows(BadRequestException.class, () -> storage.store(
                new MockMultipartFile("image", "photo.jpg", "image/jpeg", "not an image".getBytes())));
        assertThrows(BadRequestException.class, () -> storage.store(
                new MockMultipartFile("image", "photo.jpg", "image/jpeg", new byte[0])));
    }
}
