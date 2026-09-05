package com.careerinde.careerinde_app.application.coverletter;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class CoverLetterController {

    private final CoverLetterService coverLetterService;
    private final CoverLetterPdfService coverLetterPdfService;


    public CoverLetterController(
            CoverLetterService coverLetterService,
            CoverLetterPdfService coverLetterPdfService) {

        this.coverLetterService = coverLetterService;
        this.coverLetterPdfService = coverLetterPdfService;
    }


    // =========================================================
    // GENERATE COVER LETTER
    // =========================================================

    @PostMapping("/cover-letter/generate")
    public String generateCoverLetter(

            @RequestParam(
                    value = "jobTitle",
                    required = false
            )
            String jobTitle,

            @RequestParam(
                    value = "companyName",
                    required = false
            )
            String companyName,

            @RequestParam(
                    value = "jobDescription",
                    required = false
            )
            String jobDescription,

            HttpSession session,
            Model model) {

        try {

            // =================================================
            // GET CV FROM SESSION
            // =================================================

            String cvText =
                    (String) session.getAttribute(
                            "latestCvText"
                    );


            if (cvText == null || cvText.isBlank()) {

                model.addAttribute(
                        "error",
                        "Please upload your CV before generating a cover letter."
                );

                return "cover-letter";
            }


            // =================================================
            // GET JOB DESCRIPTION
            // =================================================

            String effectiveJobDescription =
                    cleanOptional(jobDescription);


            if (effectiveJobDescription == null) {

                effectiveJobDescription =
                        (String) session.getAttribute(
                                "latestJobDescription"
                        );
            }


            if (effectiveJobDescription == null
                    || effectiveJobDescription.isBlank()) {

                model.addAttribute(
                        "error",
                        "Please provide a job description before generating a cover letter."
                );

                return "cover-letter";
            }


            // =================================================
            // GET JOB TITLE
            // =================================================

            String effectiveJobTitle =
                    cleanOptional(jobTitle);


            if (effectiveJobTitle == null) {

                effectiveJobTitle =
                        (String) session.getAttribute(
                                "latestTargetJobTitle"
                        );
            }


            if (effectiveJobTitle == null
                    || effectiveJobTitle.isBlank()) {

                effectiveJobTitle =
                        "Advertised Position";
            }


            // =================================================
            // GET COMPANY NAME
            // =================================================

            String effectiveCompanyName =
                    cleanOptional(companyName);


            if (effectiveCompanyName == null) {

                effectiveCompanyName =
                        (String) session.getAttribute(
                                "latestTargetCompanyName"
                        );
            }


            if (effectiveCompanyName == null
                    || effectiveCompanyName.isBlank()) {

                effectiveCompanyName =
                        "Company";
            }


            // =================================================
            // CLEAN VALUES
            // =================================================

            effectiveJobDescription =
                    effectiveJobDescription.trim();

            effectiveJobTitle =
                    effectiveJobTitle.trim();

            effectiveCompanyName =
                    effectiveCompanyName.trim();


            // =================================================
            // SAVE JOB CONTEXT
            // =================================================

            session.setAttribute(
                    "latestJobDescription",
                    effectiveJobDescription
            );

            session.setAttribute(
                    "latestTargetJobTitle",
                    effectiveJobTitle
            );

            session.setAttribute(
                    "latestTargetCompanyName",
                    effectiveCompanyName
            );


            // =================================================
            // GENERATE COVER LETTER WITH AI
            // =================================================

            long startTime =
                    System.currentTimeMillis();


            CoverLetter coverLetter =
                    coverLetterService.generateCoverLetter(
                            cvText,
                            effectiveJobDescription,
                            effectiveJobTitle,
                            effectiveCompanyName
                    );


            long generationTime =
                    System.currentTimeMillis()
                            - startTime;


            System.out.println();
            System.out.println(
                    "=========================================="
            );
            System.out.println(
                    "CAREERINDE COVER LETTER GENERATED"
            );
            System.out.println(
                    "Generation Time: "
                            + generationTime
                            + " ms"
            );
            System.out.println(
                    "=========================================="
            );


            // =================================================
            // SAVE GENERATED COVER LETTER
            // =================================================

            session.setAttribute(
                    "latestCoverLetter",
                    coverLetter
            );


            // =================================================
            // SEND DATA TO VIEW
            // =================================================

            model.addAttribute(
                    "coverLetter",
                    coverLetter
            );

            model.addAttribute(
                    "jobTitle",
                    effectiveJobTitle
            );

            model.addAttribute(
                    "companyName",
                    effectiveCompanyName
            );

            model.addAttribute(
                    "jobDescription",
                    effectiveJobDescription
            );


            return "cover-letter";


        } catch (IllegalArgumentException exception) {

            exception.printStackTrace();

            model.addAttribute(
                    "error",
                    exception.getMessage()
            );

            addExistingSessionData(
                    session,
                    model
            );

            return "cover-letter";


        } catch (Exception exception) {

            exception.printStackTrace();

            model.addAttribute(
                    "error",
                    "Could not generate your cover letter. Please try again."
            );

            addExistingSessionData(
                    session,
                    model
            );

            return "cover-letter";
        }
    }


    // =========================================================
    // SAVE EDITED COVER LETTER
    // =========================================================

    @PostMapping("/cover-letter/save")
    public String saveCoverLetter(

            @RequestParam(
                    value = "jobTitle",
                    required = false
            )
            String jobTitle,

            @RequestParam(
                    value = "companyName",
                    required = false
            )
            String companyName,

            @RequestParam(
                    value = "subject",
                    required = false
            )
            String subject,

            @RequestParam(
                    value = "greeting",
                    required = false
            )
            String greeting,

            @RequestParam(
                    value = "body",
                    required = false
            )
            String body,

            @RequestParam(
                    value = "closing",
                    required = false
            )
            String closing,

            @RequestParam(
                    value = "candidateName",
                    required = false
            )
            String candidateName,

            HttpSession session,
            Model model) {

        try {

            // =================================================
            // VALIDATE BODY
            // =================================================

            if (body == null || body.isBlank()) {

                model.addAttribute(
                        "error",
                        "Cover letter body cannot be empty."
                );

                addExistingSessionData(
                        session,
                        model
                );

                return "cover-letter";
            }


            // =================================================
            // CREATE UPDATED COVER LETTER
            // =================================================

            CoverLetter updatedCoverLetter =
                    new CoverLetter();


            updatedCoverLetter.setJobTitle(
                    cleanValue(
                            jobTitle,
                            "Advertised Position"
                    )
            );


            updatedCoverLetter.setCompanyName(
                    cleanValue(
                            companyName,
                            "Company"
                    )
            );


            updatedCoverLetter.setSubject(
                    cleanValue(
                            subject,
                            ""
                    )
            );


            updatedCoverLetter.setGreeting(
                    cleanValue(
                            greeting,
                            "Dear Hiring Manager,"
                    )
            );


            updatedCoverLetter.setBody(
                    body.trim()
            );


            updatedCoverLetter.setClosing(
                    cleanValue(
                            closing,
                            "Kind regards,"
                    )
            );


            updatedCoverLetter.setCandidateName(
                    cleanValue(
                            candidateName,
                            ""
                    )
            );


            // =================================================
            // SAVE EDITED VERSION
            // =================================================

            session.setAttribute(
                    "latestCoverLetter",
                    updatedCoverLetter
            );


            // =================================================
            // UPDATE JOB CONTEXT
            // =================================================

            session.setAttribute(
                    "latestTargetJobTitle",
                    updatedCoverLetter.getJobTitle()
            );


            session.setAttribute(
                    "latestTargetCompanyName",
                    updatedCoverLetter.getCompanyName()
            );


            // =================================================
            // SEND DATA TO VIEW
            // =================================================

            model.addAttribute(
                    "coverLetter",
                    updatedCoverLetter
            );


            model.addAttribute(
                    "jobTitle",
                    updatedCoverLetter.getJobTitle()
            );


            model.addAttribute(
                    "companyName",
                    updatedCoverLetter.getCompanyName()
            );


            model.addAttribute(
                    "jobDescription",
                    session.getAttribute(
                            "latestJobDescription"
                    )
            );


            model.addAttribute(
                    "success",
                    "Your changes have been saved."
            );


            return "cover-letter";


        } catch (Exception exception) {

            exception.printStackTrace();

            model.addAttribute(
                    "error",
                    "Could not save your cover letter. Please try again."
            );

            addExistingSessionData(
                    session,
                    model
            );

            return "cover-letter";
        }
    }


    // =========================================================
    // DOWNLOAD COVER LETTER PDF
    // =========================================================

    @GetMapping("/cover-letter/download")
    public ResponseEntity<byte[]> downloadCoverLetter(
            HttpSession session) {

        // =====================================================
        // GET COVER LETTER
        // =====================================================

        CoverLetter coverLetter =
                (CoverLetter) session.getAttribute(
                        "latestCoverLetter"
                );


        if (coverLetter == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        // =====================================================
        // GENERATE PDF
        // =====================================================

        byte[] pdf =
                coverLetterPdfService.generatePdf(
                        coverLetter
                );


        // =====================================================
        // BUILD FILE NAME
        // =====================================================

        String jobTitle =
                sanitizeFileName(
                        coverLetter.getJobTitle()
                );


        String companyName =
                sanitizeFileName(
                        coverLetter.getCompanyName()
                );


        StringBuilder fileName =
                new StringBuilder(
                        "CareerInDe_Cover_Letter"
                );


        if (!jobTitle.isBlank()) {

            fileName
                    .append("_")
                    .append(jobTitle);
        }


        if (!companyName.isBlank()) {

            fileName
                    .append("_")
                    .append(companyName);
        }


        fileName.append(
                ".pdf"
        );


        // =====================================================
        // RETURN PDF
        // =====================================================

        return ResponseEntity
                .ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + fileName
                                + "\""
                )

                .contentType(
                        MediaType.APPLICATION_PDF
                )

                .contentLength(
                        pdf.length
                )

                .body(
                        pdf
                );
    }


    // =========================================================
    // ADD EXISTING SESSION DATA TO VIEW
    // =========================================================

    private void addExistingSessionData(
            HttpSession session,
            Model model) {

        model.addAttribute(
                "coverLetter",
                session.getAttribute(
                        "latestCoverLetter"
                )
        );

        model.addAttribute(
                "jobTitle",
                session.getAttribute(
                        "latestTargetJobTitle"
                )
        );

        model.addAttribute(
                "companyName",
                session.getAttribute(
                        "latestTargetCompanyName"
                )
        );

        model.addAttribute(
                "jobDescription",
                session.getAttribute(
                        "latestJobDescription"
                )
        );
    }


    // =========================================================
    // CLEAN OPTIONAL VALUE
    // =========================================================

    private String cleanOptional(
            String value) {

        if (value == null
                || value.isBlank()) {

            return null;
        }

        return value.trim();
    }


    // =========================================================
    // CLEAN FORM VALUE
    // =========================================================

    private String cleanValue(
            String value,
            String fallback) {

        if (value == null
                || value.isBlank()) {

            return fallback;
        }

        return value.trim();
    }


    // =========================================================
    // SAFE PDF FILE NAME
    // =========================================================

    private String sanitizeFileName(
            String value) {

        if (value == null
                || value.isBlank()) {

            return "";
        }

        return value
                .trim()

                .replaceAll(
                        "[^a-zA-Z0-9-_]",
                        "_"
                )

                .replaceAll(
                        "_+",
                        "_"
                )

                .replaceAll(
                        "^_+|_+$",
                        ""
                );
    }
}