package com.careerinde.careerinde_app.application;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.careerinde.careerinde_app.user.User;
import com.careerinde.careerinde_app.user.UserRepository;

@Controller
public class ApplicationDocumentController {

    private final ApplicationDocumentService documentService;

    private final UserRepository userRepository;


    public ApplicationDocumentController(
            ApplicationDocumentService documentService,
            UserRepository userRepository) {

        this.documentService =
                documentService;

        this.userRepository =
                userRepository;
    }


    // =========================================================
    // CURRENT USER
    // =========================================================

    private User getCurrentUser(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "User is not authenticated");
        }

        return userRepository
                .findByEmail(
                        authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"));
    }


    // =========================================================
    // UPLOAD
    // =========================================================

    @PostMapping(
            "/applications/{applicationId}/documents")
    public String uploadDocument(

            @PathVariable
            Long applicationId,

            @RequestParam("file")
            MultipartFile file,

            @RequestParam(
                    value = "documentType",
                    required = false)
            DocumentType documentType,

            Authentication authentication,

            RedirectAttributes redirectAttributes) {

        User user =
                getCurrentUser(
                        authentication);

        try {

            documentService.upload(
                    applicationId,
                    file,
                    documentType,
                    user);

            redirectAttributes
                    .addFlashAttribute(
                            "successMessage",
                            "Document uploaded successfully.");

        } catch (IllegalArgumentException exception) {

            redirectAttributes
                    .addFlashAttribute(
                            "errorMessage",
                            exception.getMessage());

        } catch (IOException exception) {

            redirectAttributes
                    .addFlashAttribute(
                            "errorMessage",
                            "The document could not be stored.");

        }

        return "redirect:/applications/"
                + applicationId;
    }


    // =========================================================
    // DOWNLOAD
    // =========================================================

    @GetMapping(
            "/applications/{applicationId}/documents/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(

            @PathVariable
            Long applicationId,

            @PathVariable
            Long documentId,

            Authentication authentication)
            throws IOException {

        User user =
                getCurrentUser(
                        authentication);

        /*
         * First verify that the application belongs
         * to the current user.
         */
        ApplicationDocument document =
                documentService
                        .getDocument(
                                documentId,
                                user);

        /*
         * Also ensure that the requested document
         * actually belongs to the application ID
         * contained in the URL.
         */
        if (!document
                .getApplication()
                .getId()
                .equals(applicationId)) {

            throw new IllegalArgumentException(
                    "Document not found");
        }


        Resource resource =
                documentService
                        .loadFile(
                                documentId,
                                user);


        String contentType =
                document.getContentType();

        MediaType mediaType;

        try {

            mediaType =
                    MediaType.parseMediaType(
                            contentType);

        } catch (Exception exception) {

            mediaType =
                    MediaType.APPLICATION_OCTET_STREAM;
        }


        ContentDisposition disposition =
                ContentDisposition
                        .attachment()
                        .filename(
                                document
                                        .getOriginalFileName())
                        .build();


        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        disposition.toString())
                .contentLength(
                        document.getFileSize())
                .body(resource);
    }


    // =========================================================
    // DELETE
    // =========================================================

    @PostMapping(
            "/applications/{applicationId}/documents/{documentId}/delete")
    public String deleteDocument(

            @PathVariable
            Long applicationId,

            @PathVariable
            Long documentId,

            Authentication authentication,

            RedirectAttributes redirectAttributes) {

        User user =
                getCurrentUser(
                        authentication);

        try {

            ApplicationDocument document =
                    documentService
                            .getDocument(
                                    documentId,
                                    user);


            if (!document
                    .getApplication()
                    .getId()
                    .equals(applicationId)) {

                throw new IllegalArgumentException(
                        "Document not found");
            }


            documentService
                    .deleteDocument(
                            documentId,
                            user);


            redirectAttributes
                    .addFlashAttribute(
                            "successMessage",
                            "Document deleted successfully.");

        } catch (IllegalArgumentException exception) {

            redirectAttributes
                    .addFlashAttribute(
                            "errorMessage",
                            exception.getMessage());

        } catch (IOException exception) {

            redirectAttributes
                    .addFlashAttribute(
                            "errorMessage",
                            "The document could not be deleted.");

        }


        return "redirect:/applications/"
                + applicationId;
    }
}