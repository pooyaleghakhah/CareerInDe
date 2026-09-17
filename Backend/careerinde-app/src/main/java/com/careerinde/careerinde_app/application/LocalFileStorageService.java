package com.careerinde.careerinde_app.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileStorageService
        implements FileStorageService {

    private final Path storageDirectory;

    public LocalFileStorageService(
            @Value("${app.storage.application-documents:uploads/applications}")
            String storagePath)
            throws IOException {

        this.storageDirectory =
                Paths.get(storagePath)
                        .toAbsolutePath()
                        .normalize();

        Files.createDirectories(
                this.storageDirectory);
    }


    @Override
    public String store(
            MultipartFile file)
            throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "File cannot be empty");
        }

        String extension =
                getExtension(
                        file.getOriginalFilename());

        String storedFileName =
                UUID.randomUUID()
                        + extension;

        Path destination =
                storageDirectory
                        .resolve(storedFileName)
                        .normalize();

        if (!destination.startsWith(
                storageDirectory)) {

            throw new SecurityException(
                    "Invalid file path");
        }

        Files.copy(
                file.getInputStream(),
                destination,
                StandardCopyOption.REPLACE_EXISTING);

        return storedFileName;
    }


    @Override
    public Resource load(
            String storedFileName)
            throws IOException {

        Path file =
                storageDirectory
                        .resolve(storedFileName)
                        .normalize();

        if (!file.startsWith(
                storageDirectory)) {

            throw new SecurityException(
                    "Invalid file path");
        }

        Resource resource =
                new UrlResource(
                        file.toUri());

        if (!resource.exists()
                || !resource.isReadable()) {

            throw new IOException(
                    "File not found");
        }

        return resource;
    }


    @Override
    public void delete(
            String storedFileName)
            throws IOException {

        Path file =
                storageDirectory
                        .resolve(storedFileName)
                        .normalize();

        if (!file.startsWith(
                storageDirectory)) {

            throw new SecurityException(
                    "Invalid file path");
        }

        Files.deleteIfExists(file);
    }


    private String getExtension(
            String originalFileName) {

        if (originalFileName == null) {
            return "";
        }

        String safeName =
                Paths.get(originalFileName)
                        .getFileName()
                        .toString();

        int index =
                safeName.lastIndexOf('.');

        if (index < 0) {
            return "";
        }

        return safeName
                .substring(index)
                .toLowerCase();
    }
}