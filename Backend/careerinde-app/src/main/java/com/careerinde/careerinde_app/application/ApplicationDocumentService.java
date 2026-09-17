package com.careerinde.careerinde_app.application;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.careerinde.careerinde_app.user.User;

@Service
@Transactional
public class ApplicationDocumentService {

    private static final long MAX_FILE_SIZE =
            10 * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of(
                    "application/pdf",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            );

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of(
                    ".pdf",
                    ".docx"
            );


    private final ApplicationDocumentRepository documentRepository;

    private final JobApplicationService applicationService;

    private final FileStorageService fileStorageService;


    public ApplicationDocumentService(
            ApplicationDocumentRepository documentRepository,
            JobApplicationService applicationService,
            FileStorageService fileStorageService) {

        this.documentRepository =
                documentRepository;

        this.applicationService =
                applicationService;

        this.fileStorageService =
                fileStorageService;
    }


    // =========================================================
    // LIST
    // =========================================================

    @Transactional(readOnly = true)
    public List<ApplicationDocument> getDocuments(
            Long applicationId,
            User user) {

        JobApplication application =
                applicationService
                        .getApplication(
                                applicationId,
                                user);

        return documentRepository
                .findByApplicationAndApplicationUserOrderByUploadedAtDesc(
                        application,
                        user);
    }


    // =========================================================
    // GET DOCUMENT
    // =========================================================

    @Transactional(readOnly = true)
    public ApplicationDocument getDocument(
            Long documentId,
            User user) {

        return documentRepository
                .findByIdAndApplicationUser(
                        documentId,
                        user)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Document not found"));
    }


    // =========================================================
    // UPLOAD
    // =========================================================

    public ApplicationDocument upload(
            Long applicationId,
            MultipartFile file,
            DocumentType documentType,
            User user)
            throws IOException {

        JobApplication application =
                applicationService
                        .getApplication(
                                applicationId,
                                user);

        validateFile(file);

        if (documentType == null) {
            documentType =
                    DocumentType.OTHER;
        }

        String storedFileName = null;

        try {

            storedFileName =
                    fileStorageService
                            .store(file);

            ApplicationDocument document =
                    new ApplicationDocument();

            document.setOriginalFileName(
                    sanitizeOriginalFileName(
                            file.getOriginalFilename()));

            document.setStoredFileName(
                    storedFileName);

            document.setContentType(
                    file.getContentType());

            document.setFileSize(
                    file.getSize());

            document.setDocumentType(
                    documentType);

            document.setApplication(
                    application);

            return documentRepository
                    .save(document);

        } catch (Exception exception) {

            if (storedFileName != null) {

                try {
                    fileStorageService
                            .delete(storedFileName);
                } catch (IOException ignored) {
                    // Avoid hiding the original exception.
                }
            }

            throw exception;
        }
    }


    // =========================================================
    // DOWNLOAD
    // =========================================================

    @Transactional(readOnly = true)
    public Resource loadFile(
            Long documentId,
            User user)
            throws IOException {

        ApplicationDocument document =
                getDocument(
                        documentId,
                        user);

        return fileStorageService
                .load(
                        document.getStoredFileName());
    }


    // =========================================================
    // DELETE
    // =========================================================

    public void deleteDocument(
            Long documentId,
            User user)
            throws IOException {

        ApplicationDocument document =
                getDocument(
                        documentId,
                        user);

        fileStorageService
                .delete(
                        document.getStoredFileName());

        documentRepository
                .delete(document);
    }


    // =========================================================
    // VALIDATION
    // =========================================================

    private void validateFile(
            MultipartFile file) {

        if (file == null
                || file.isEmpty()) {

            throw new IllegalArgumentException(
                    "Please select a file");
        }


        if (file.getSize()
                > MAX_FILE_SIZE) {

            throw new IllegalArgumentException(
                    "Maximum file size is 10 MB");
        }


        String originalFileName =
                sanitizeOriginalFileName(
                        file.getOriginalFilename());

        String extension =
                getExtension(
                        originalFileName);

        if (!ALLOWED_EXTENSIONS
                .contains(extension)) {

            throw new IllegalArgumentException(
                    "Only PDF and DOCX files are allowed");
        }


        String contentType =
                file.getContentType();

        if (contentType == null
                || !ALLOWED_CONTENT_TYPES
                        .contains(contentType)) {

            throw new IllegalArgumentException(
                    "Unsupported file type");
        }
    }


    private String sanitizeOriginalFileName(
            String originalFileName) {

        if (originalFileName == null
                || originalFileName.isBlank()) {

            return "document";
        }

        return Paths
                .get(originalFileName)
                .getFileName()
                .toString()
                .replaceAll(
                        "[\\r\\n]",
                        "");
    }


    private String getExtension(
            String fileName) {

        int index =
                fileName.lastIndexOf('.');

        if (index < 0) {
            return "";
        }

        return fileName
                .substring(index)
                .toLowerCase();
    }
}