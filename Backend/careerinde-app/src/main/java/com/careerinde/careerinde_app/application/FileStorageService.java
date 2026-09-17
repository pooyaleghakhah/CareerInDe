package com.careerinde.careerinde_app.application;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String store(MultipartFile file)
            throws IOException;

    Resource load(String storedFileName)
            throws IOException;

    void delete(String storedFileName)
            throws IOException;
}