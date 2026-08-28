package com.careerinde.careerinde_app.resume.export;

import java.nio.charset.StandardCharsets;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.careerinde.careerinde_app.resume.optimization.OptimizedResume;

@Controller
public class ResumePdfController {

    private final ResumePdfService resumePdfService;


    public ResumePdfController(
            ResumePdfService resumePdfService) {

        this.resumePdfService =
                resumePdfService;
    }


    @GetMapping("/resume/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(
            HttpSession session) {


        Object sessionResume =
                session.getAttribute(
                        "optimizedResume"
                );


        if (!(sessionResume instanceof OptimizedResume resume)) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "No optimized resume found."
                                    .getBytes(
                                            StandardCharsets.UTF_8
                                    )
                    );
        }


        byte[] pdf =
                resumePdfService
                        .generatePdf(
                                resume
                        );


        String filename =
                createFilename(
                        resume
                );


        HttpHeaders headers =
                new HttpHeaders();


        headers.setContentType(
                MediaType.APPLICATION_PDF
        );


        headers.setContentDisposition(
                ContentDisposition
                        .attachment()
                        .filename(
                                filename,
                                StandardCharsets.UTF_8
                        )
                        .build()
        );


        headers.setContentLength(
                pdf.length
        );


        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdf);
    }


    private String createFilename(
            OptimizedResume resume) {


        String name =
                sanitize(
                        resume.getFullName()
                );


        String role =
                sanitize(
                        resume.getTargetRole()
                );


        if (name.isBlank()) {

            name = "Resume";
        }


        if (role.isBlank()) {

            return name
                    + "_CareerInDe.pdf";
        }


        return name
                + "_"
                + role
                + "_CareerInDe.pdf";
    }


    private String sanitize(
            String value) {


        if (value == null) {
            return "";
        }


        return value
                .trim()
                .replaceAll(
                        "[^a-zA-Z0-9]+",
                        "_"
                )
                .replaceAll(
                        "^_+|_+$",
                        ""
                );
    }
}