package com.careerinde.careerinde_app.resume;

import java.io.IOException;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.careerinde.careerinde_app.ai.CVOptimizationService;

@Controller
public class CVOptimizationController {

    private final CVOptimizationService cvOptimizationService;
    private final OptimizedCvPdfService optimizedCvPdfService;


    // =========================================
    // Constructor
    // =========================================

    public CVOptimizationController(
            CVOptimizationService cvOptimizationService,
            OptimizedCvPdfService optimizedCvPdfService) {

        this.cvOptimizationService = cvOptimizationService;
        this.optimizedCvPdfService = optimizedCvPdfService;
    }


    // =========================================
    // Optimize CV with AI
    // =========================================

    @PostMapping("/resume/optimize")
    public String optimizeCV(
            @RequestParam("cvText") String cvText,
            Model model) {

        // Check original CV text
        if (cvText == null || cvText.isBlank()) {

            model.addAttribute(
                    "error",
                    "CV text is empty. Please upload your CV again."
            );

            return "cv-optimized";
        }


        // Send CV to AI optimization service
        String optimizedCV =
                cvOptimizationService.optimizeCV(cvText);


        // Send optimized CV to Thymeleaf
        model.addAttribute(
                "optimizedCV",
                optimizedCV
        );


        // Keep original CV available if needed
        model.addAttribute(
                "originalCV",
                cvText
        );


        return "cv-optimized";
    }


    // =========================================
    // Download optimized CV as PDF
    // =========================================

    @PostMapping("/resume/optimized/download")
    public ResponseEntity<byte[]> downloadOptimizedCV(
            @RequestParam("optimizedCV") String optimizedCV)
            throws IOException {


        // Check optimized CV
        if (optimizedCV == null ||
                optimizedCV.isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .build();
        }


        // Generate PDF
        byte[] pdfBytes =
                optimizedCvPdfService.generatePdf(
                        optimizedCV
                );


        // Download filename
        String fileName =
                "CareerInDe-Optimized-CV.pdf";


        // HTTP headers
        HttpHeaders headers =
                new HttpHeaders();


        headers.setContentType(
                MediaType.APPLICATION_PDF
        );


        headers.setContentDisposition(
                ContentDisposition
                        .attachment()
                        .filename(fileName)
                        .build()
        );


        headers.setContentLength(
                pdfBytes.length
        );


        // Return PDF to browser
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfBytes);
    }
}