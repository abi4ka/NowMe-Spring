package com.abik.nowme.module.nowme.service;

import com.abik.nowme.module.shared.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

/**
 * Stores post images on disk under {@code uploads/}. The file type is detected from the
 * content (magic bytes), never from the client's filename, and the server picks the name.
 */
@Slf4j
@Component
public class ImageStorage {

    static final Path UPLOAD_DIR = Paths.get("uploads");

    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'};
    private static final byte[] RIFF = {'R', 'I', 'F', 'F'};
    private static final byte[] WEBP = {'W', 'E', 'B', 'P'};

    public String store(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new BadRequestException("INVALID_IMAGE");
        }

        String extension = detectExtension(image);
        String fileName = UUID.randomUUID() + extension;
        try {
            Files.createDirectories(UPLOAD_DIR);
            try (InputStream in = image.getInputStream()) {
                Files.copy(in, UPLOAD_DIR.resolve(fileName));
            }
        } catch (IOException e) {
            throw new RuntimeException("FAILED_TO_SAVE_IMAGE", e);
        }
        return fileName;
    }

    public Path resolve(String fileName) {
        return UPLOAD_DIR.resolve(fileName);
    }

    public void delete(String fileName) {
        try {
            Files.deleteIfExists(resolve(fileName));
        } catch (IOException e) {
            log.warn("Could not delete image {}", fileName, e);
        }
    }

    private String detectExtension(MultipartFile image) {
        byte[] header = new byte[12];
        int read;
        try (InputStream in = image.getInputStream()) {
            read = in.readNBytes(header, 0, header.length);
        } catch (IOException e) {
            throw new BadRequestException("INVALID_IMAGE", e);
        }

        if (startsWith(header, read, 0, JPEG)) {
            return ".jpg";
        }
        if (startsWith(header, read, 0, PNG)) {
            return ".png";
        }
        if (startsWith(header, read, 0, RIFF) && startsWith(header, read, 8, WEBP)) {
            return ".webp";
        }
        throw new BadRequestException("INVALID_IMAGE");
    }

    private static boolean startsWith(byte[] data, int length, int offset, byte[] prefix) {
        return length >= offset + prefix.length
                && Arrays.equals(data, offset, offset + prefix.length, prefix, 0, prefix.length);
    }
}
