package org.example.lmsbackend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    public String saveFile(MultipartFile file, String subFolder) {
        try {
            String uploadDir = "uploads/" + subFolder;
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String cleanedFilename = originalFilename != null ? originalFilename.replaceAll("\\s+", "_") : "file";
            String filename = UUID.randomUUID() + "_" + cleanedFilename;

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return subFolder + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file", e);
        }
    }
}
