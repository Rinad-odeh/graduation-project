package com.baligh.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${app.storage.upload-dir}")
    private String uploadDir;

    public String store(MultipartFile file, Long issueId) {
        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize()
                    .resolve("issues").resolve(String.valueOf(issueId));
            Files.createDirectories(dir);

            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf('.'));
            }

            String filename = UUID.randomUUID() + ext;
            Path dest = dir.resolve(filename);
            file.transferTo(dest.toFile());

            return "/uploads/issues/" + issueId + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }

    public void delete(String url) {
        try {
            Path path = Paths.get(uploadDir).toAbsolutePath().normalize()
                    .resolve(url.replaceFirst("^/uploads/", ""));
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // log but don't throw — deletion is best-effort
        }
    }
}
