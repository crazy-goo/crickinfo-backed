package com.debu.crickInfo.service;

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

    private static final String ROOT_UPLOAD_DIR = "src/main/resources/static/uploads";

    public String store(MultipartFile file, String subDirectory) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        Path uploadPath = Paths.get(ROOT_UPLOAD_DIR, subDirectory);
        Files.createDirectories(uploadPath);

        String originalFilename = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename().replaceAll("\\s+", "_");
        String storedFilename = UUID.randomUUID() + "_" + originalFilename;

        Path targetPath = uploadPath.resolve(storedFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + subDirectory + "/" + storedFilename;
    }
}
