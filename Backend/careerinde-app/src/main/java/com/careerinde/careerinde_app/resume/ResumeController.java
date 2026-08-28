package com.careerinde.careerinde_app.resume;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.careerinde.careerinde_app.analysis.Analysis;
import com.careerinde.careerinde_app.analysis.AnalysisRepository;
import com.careerinde.careerinde_app.ats.AtsAnalyzerService;
import com.careerinde.careerinde_app.ats.RecommendationService;

@Controller
public class ResumeController {

    private final PdfService pdfService;
    private final AtsAnalyzerService atsAnalyzerService;
    private final RecommendationService recommendationService;
    private final AnalysisRepository analysisRepository;


    public ResumeController(
            PdfService pdfService,
            AtsAnalyzerService atsAnalyzerService,
            RecommendationService recommendationService,
            AnalysisRepository analysisRepository) {

        this.pdfService = pdfService;

        this.atsAnalyzerService =
                atsAnalyzerService;

        this.recommendationService =
                recommendationService;

        this.analysisRepository =
                analysisRepository;
    }


    // =========================================================
    // UPLOAD PAGE
    // =========================================================

    @GetMapping("/resume/upload")
    public String uploadPage() {

        return "resume-upload";
    }


    // =========================================================
    // UPLOAD + LOCAL ATS ANALYSIS
    // =========================================================

    @PostMapping("/resume/upload")
    public String uploadResume(

            @RequestParam("file")
            MultipartFile file,

            Model model,

            HttpSession session)

            throws IOException {


        // =====================================================
        // VALIDATE FILE
        // =====================================================

        if (file == null ||
                file.isEmpty()) {

            model.addAttribute(
                    "error",
                    "Please select a CV file."
            );

            return "resume-upload";
        }


        // =====================================================
        // VALIDATE PDF
        // =====================================================

        String fileName =
                file.getOriginalFilename();


        if (fileName == null ||
                fileName.isBlank()) {

            fileName = "resume.pdf";
        }


        if (!fileName
                .toLowerCase()
                .endsWith(".pdf")) {

            model.addAttribute(
                    "error",
                    "Please upload a PDF file."
            );

            return "resume-upload";
        }


        // =====================================================
        // CREATE UPLOAD DIRECTORY
        // =====================================================

        String uploadDir =
                System.getProperty("user.dir")
                        + File.separator
                        + "uploads";


        File directory =
                new File(uploadDir);


        if (!directory.exists() &&
                !directory.mkdirs()) {

            throw new IOException(
                    "Could not create upload directory."
            );
        }


        // =====================================================
        // SAVE PDF
        // =====================================================

        File destination =
                new File(
                        directory,
                        fileName
                );


        file.transferTo(destination);


        // =====================================================
        // EXTRACT TEXT
        // =====================================================

        String extractedText =
                pdfService.extractText(
                        destination
                );


        if (extractedText == null ||
                extractedText.isBlank()) {

            model.addAttribute(
                    "error",
                    "We could not extract text from this CV."
            );

            return "resume-upload";
        }


        // =====================================================
        // SESSION
        // =====================================================

        session.setAttribute(
                "latestCvText",
                extractedText
        );


        session.setAttribute(
                "latestCvFileName",
                fileName
        );


        // =====================================================
        // LOCAL ATS ANALYSIS
        // NO AI API CALL HERE
        // =====================================================

        List<String> skills =
                atsAnalyzerService.detectSkills(
                        extractedText
                );


        List<String> missingSkills =
                atsAnalyzerService.missingSkills(
                        extractedText
                );


        List<String> suggestions =
                recommendationService
                        .generateSuggestions(
                                extractedText
                        );


        // =====================================================
        // LOCAL ATS SCORE
        // =====================================================

        int atsScore =
                calculateLocalAtsScore(
                        skills,
                        missingSkills,
                        extractedText
                );


        // =====================================================
        // BASIC PROFILE INFORMATION
        // =====================================================

        String profileLevel =
                determineProfileLevel(
                        atsScore
                );


        String bestJobMatch =
                determineBestJobMatch(
                        skills
                );


        // =====================================================
        // SAVE ANALYSIS
        // =====================================================

        Analysis analysis =
                new Analysis();


        analysis.setAtsScore(
                atsScore
        );


        analysis.setDetectedSkills(
                String.join(
                        ", ",
                        skills
                )
        );


        analysis.setMissingSkills(
                String.join(
                        ", ",
                        missingSkills
                )
        );


        analysis.setSuggestions(
                String.join(
                        ", ",
                        suggestions
                )
        );


        analysis.setCvText(
                extractedText
        );


        analysisRepository.save(
                analysis
        );


        // =====================================================
        // SEND TO VIEW
        // =====================================================

        model.addAttribute(
                "atsScore",
                atsScore
        );


        model.addAttribute(
                "profileLevel",
                profileLevel
        );


        model.addAttribute(
                "bestJobMatch",
                bestJobMatch
        );


        model.addAttribute(
                "strengths",
                skills
        );


        model.addAttribute(
                "skills",
                skills
        );


        model.addAttribute(
                "missingSkills",
                missingSkills
        );


        model.addAttribute(
                "recommendations",
                suggestions
        );


        model.addAttribute(
                "cvText",
                extractedText
        );


        System.out.println();
        System.out.println(
                "======================================"
        );

        System.out.println(
                "CAREERINDE CV UPLOAD"
        );

        System.out.println(
                "AI Provider: NONE"
        );

        System.out.println(
                "CV length: "
                        + extractedText.length()
        );

        System.out.println(
                "ATS Score: "
                        + atsScore
        );

        System.out.println(
                "Skills detected: "
                        + skills.size()
        );

        System.out.println(
                "======================================"
        );


        return "cv-result";
    }


    // =========================================================
    // LOCAL ATS SCORE
    // =========================================================

    private int calculateLocalAtsScore(
            List<String> skills,
            List<String> missingSkills,
            String cvText) {


        int score = 35;


        // -----------------------------------------------------
        // SKILLS
        // -----------------------------------------------------

        if (skills != null) {

            score += Math.min(
                    skills.size() * 4,
                    30
            );
        }


        // -----------------------------------------------------
        // CV LENGTH
        // -----------------------------------------------------

        if (cvText != null) {

            int length =
                    cvText.length();


            if (length >= 1500) {

                score += 5;
            }


            if (length >= 3000) {

                score += 5;
            }


            if (length >= 4500) {

                score += 5;
            }
        }


        // -----------------------------------------------------
        // COMMON CV SECTIONS
        // -----------------------------------------------------

        String lower =
                cvText == null
                        ? ""
                        : cvText.toLowerCase();


        if (containsAny(
                lower,
                "experience",
                "work experience",
                "professional experience",
                "berufserfahrung")) {

            score += 5;
        }


        if (containsAny(
                lower,
                "education",
                "academic",
                "ausbildung",
                "studium")) {

            score += 5;
        }


        if (containsAny(
                lower,
                "skills",
                "technical skills",
                "technologies",
                "kenntnisse")) {

            score += 5;
        }


        // -----------------------------------------------------
        // MISSING SKILL PENALTY
        // -----------------------------------------------------

        if (missingSkills != null) {

            score -= Math.min(
                    missingSkills.size(),
                    10
            );
        }


        return Math.max(
                0,
                Math.min(
                        score,
                        100
                )
        );
    }


    // =========================================================
    // PROFILE LEVEL
    // =========================================================

    private String determineProfileLevel(
            int atsScore) {


        if (atsScore >= 80) {

            return "Strong";
        }


        if (atsScore >= 60) {

            return "Good";
        }


        if (atsScore >= 40) {

            return "Developing";
        }


        return "Needs Improvement";
    }


    // =========================================================
    // BASIC JOB MATCH
    // =========================================================

    private String determineBestJobMatch(
            List<String> skills) {


        if (skills == null ||
                skills.isEmpty()) {

            return "General IT Position";
        }


        String joined =
                String.join(
                        " ",
                        skills
                ).toLowerCase();


        if (joined.contains("java") ||
                joined.contains("spring")) {

            return "Java Backend Developer";
        }


        if (joined.contains("python")) {

            return "Python Developer";
        }


        if (joined.contains("power bi") ||
                joined.contains("sql")) {

            return "Data Analyst";
        }


        if (joined.contains("angular") ||
                joined.contains("javascript")) {

            return "Frontend Developer";
        }


        return "Software Developer";
    }


    // =========================================================
    // TEXT HELPER
    // =========================================================

    private boolean containsAny(
            String text,
            String... values) {


        for (String value : values) {

            if (text.contains(value)) {

                return true;
            }
        }


        return false;
    }
}