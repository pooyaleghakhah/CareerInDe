package com.careerinde.careerinde_app.application.coverletter;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.careerinde.careerinde_app.ai.OpenAIService;
import com.careerinde.careerinde_app.ai.gemini.GeminiAIService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CoverLetterService {

    private final GeminiAIService geminiAIService;
    private final OpenAIService groqService;
    private final ObjectMapper objectMapper;

    private static final int MAX_CV_LENGTH = 6000;
    private static final int MAX_JOB_DESCRIPTION_LENGTH = 2500;

    private static final int GEMINI_MAX_TOKENS = 1800;
    private static final int GROQ_MAX_TOKENS = 2000;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public CoverLetterService(
            GeminiAIService geminiAIService,
            OpenAIService groqService,
            ObjectMapper objectMapper) {

        this.geminiAIService = geminiAIService;
        this.groqService = groqService;
        this.objectMapper = objectMapper;
    }


    // =========================================================
    // GENERATE COVER LETTER
    // =========================================================

    public CoverLetter generateCoverLetter(
            String cvText,
            String jobDescription,
            String jobTitle,
            String companyName) {

        validateInput(cvText, jobDescription);

        String safeCvText =
                limitText(
                        cvText,
                        MAX_CV_LENGTH
                );

        String safeJobDescription =
                limitText(
                        jobDescription,
                        MAX_JOB_DESCRIPTION_LENGTH
                );

        String safeJobTitle =
                normalizeOptional(
                        jobTitle,
                        "Advertised Position"
                );

        String safeCompanyName =
                normalizeOptional(
                        companyName,
                        "Company"
                );

        String prompt =
                buildPrompt(
                        safeCvText,
                        safeJobDescription,
                        safeJobTitle,
                        safeCompanyName
                );

        Map<String, Object> schema =
                buildResponseSchema();


        System.out.println();
        System.out.println("==========================================");
        System.out.println("CAREERINDE COVER LETTER AI");
        System.out.println("Primary Provider: Gemini");
        System.out.println("Fallback Provider: Groq");
        System.out.println("Job Title: " + safeJobTitle);
        System.out.println("Company: " + safeCompanyName);
        System.out.println("==========================================");


        String response;


        // =====================================================
        // PRIMARY: GEMINI
        // =====================================================

        try {

            long start =
                    System.currentTimeMillis();

            response =
                    geminiAIService.generateJson(
                            prompt,
                            schema,
                            0.15,
                            GEMINI_MAX_TOKENS
                    );

            long duration =
                    System.currentTimeMillis() - start;

            System.out.println();
            System.out.println("==========================================");
            System.out.println("COVER LETTER PROVIDER: GEMINI");
            System.out.println("Generation Time: " + duration + " ms");
            System.out.println("==========================================");


        } catch (Exception geminiException) {


            // =================================================
            // FALLBACK: GROQ
            // =================================================

            System.err.println();
            System.err.println("==========================================");
            System.err.println("GEMINI COVER LETTER FAILED");
            System.err.println(
                    "Reason: "
                            + geminiException.getMessage()
            );
            System.err.println("Switching to Groq fallback...");
            System.err.println("==========================================");


            try {

                long start =
                        System.currentTimeMillis();

                response =
                        groqService.sendPrompt(
                                prompt,
                                GROQ_MAX_TOKENS
                        );

                long duration =
                        System.currentTimeMillis() - start;

                System.out.println();
                System.out.println("==========================================");
                System.out.println(
                        "COVER LETTER PROVIDER: GROQ FALLBACK"
                );
                System.out.println(
                        "Generation Time: "
                                + duration
                                + " ms"
                );
                System.out.println("==========================================");


            } catch (Exception groqException) {

                System.err.println();
                System.err.println("==========================================");
                System.err.println("ALL COVER LETTER PROVIDERS FAILED");
                System.err.println(
                        "Gemini: "
                                + geminiException.getMessage()
                );
                System.err.println(
                        "Groq: "
                                + groqException.getMessage()
                );
                System.err.println("==========================================");

                throw new IllegalStateException(
                        "AI cover letter generation is temporarily unavailable.",
                        groqException
                );
            }
        }


        // =====================================================
        // PARSE RESPONSE
        // =====================================================

        return parseResponse(response);
    }


    // =========================================================
    // GEMINI RESPONSE SCHEMA
    // =========================================================

    private Map<String, Object> buildResponseSchema() {

        Map<String, Object> stringSchema =
                Map.of(
                        "type",
                        "STRING"
                );

        Map<String, Object> properties =
                Map.of(
                        "jobTitle",
                        stringSchema,

                        "companyName",
                        stringSchema,

                        "subject",
                        stringSchema,

                        "greeting",
                        stringSchema,

                        "body",
                        stringSchema,

                        "closing",
                        stringSchema,

                        "candidateName",
                        stringSchema
                );

        return Map.of(
                "type",
                "OBJECT",

                "properties",
                properties,

                "required",
                List.of(
                        "jobTitle",
                        "companyName",
                        "subject",
                        "greeting",
                        "body",
                        "closing",
                        "candidateName"
                )
        );
    }


    // =========================================================
    // COVER LETTER PROMPT
    // =========================================================

    private String buildPrompt(
            String cvText,
            String jobDescription,
            String jobTitle,
            String companyName) {

        return """
You are CareerInDe's professional cover letter generator.

Create a concise, personalized cover letter for the target job.

Use ONLY facts explicitly supported by the candidate CV.
The job description describes the employer's requirements
and MUST NOT be treated as evidence about the candidate.

Never invent:
- skills or technologies
- employers or positions
- work experience
- dates or years of experience
- education or completed degrees
- certifications
- projects
- achievements or metrics
- language proficiency

Do not move a skill from one CV context to another.
For example, a technology listed under Skills or Projects
must not be attributed to an employer unless the CV says so.

If a degree is still in progress, describe it as ongoing,
never as completed.

If a claim cannot be verified from the CV, omit it.

TARGET:
Job Title: %s
Company: %s

Write in the primary language of the job description.

German:
- professional natural German
- formal "Sie"
- default greeting: "Sehr geehrte Damen und Herren,"
- default closing: "Mit freundlichen Grüßen"

English:
- professional business English
- default greeting: "Dear Hiring Team,"
- professional closing

Never invent a contact person.

CONTENT:
- approximately 200-280 words
- 3-4 short paragraphs
- role-specific
- connect important job requirements with verified CV evidence
- credible motivation
- no clichés or keyword stuffing
- no exaggerated seniority
- do not simply repeat the CV

Before answering, verify every candidate claim against the CV.

Return ONLY valid JSON with exactly these fields:

{
  "jobTitle": "%s",
  "companyName": "%s",
  "subject": "...",
  "greeting": "...",
  "body": "...",
  "closing": "...",
  "candidateName": "..."
}

Rules:
- jobTitle must be exactly "%s"
- companyName must be exactly "%s"
- body contains only the letter body
- candidateName must come from the CV or be empty
- no Markdown
- no code fences
- no text outside JSON

CANDIDATE CV:
%s

JOB DESCRIPTION:
%s
"""
                .formatted(
                        jobTitle,
                        companyName,
                        jobTitle,
                        companyName,
                        jobTitle,
                        companyName,
                        cvText,
                        jobDescription
                );
    }


    // =========================================================
    // PARSE RESPONSE
    // =========================================================

    private CoverLetter parseResponse(
            String response) {

        if (response == null
                || response.isBlank()) {

            throw new IllegalStateException(
                    "AI returned an empty cover letter."
            );
        }

        try {

            String cleaned =
                    cleanJson(response);

            CoverLetter coverLetter =
                    objectMapper.readValue(
                            cleaned,
                            CoverLetter.class
                    );

            validateGeneratedCoverLetter(
                    coverLetter
            );

            return coverLetter;

        } catch (Exception exception) {

            System.err.println();
            System.err.println("==========================================");
            System.err.println("COVER LETTER JSON PARSE FAILED");
            System.err.println(
                    "Response Length: "
                            + response.length()
            );
            System.err.println(
                    "Reason: "
                            + exception.getMessage()
            );
            System.err.println("==========================================");

            throw new IllegalStateException(
                    "Could not parse generated cover letter.",
                    exception
            );
        }
    }


    // =========================================================
    // CLEAN JSON
    // =========================================================

    private String cleanJson(
            String response) {

        if (response == null
                || response.isBlank()) {

            return "";
        }

        String cleaned =
                response.trim();

        if (cleaned.startsWith("```json")) {

            cleaned =
                    cleaned
                            .substring(7)
                            .trim();

        } else if (cleaned.startsWith("```")) {

            cleaned =
                    cleaned
                            .substring(3)
                            .trim();
        }

        if (cleaned.endsWith("```")) {

            cleaned =
                    cleaned
                            .substring(
                                    0,
                                    cleaned.length() - 3
                            )
                            .trim();
        }

        int firstBrace =
                cleaned.indexOf('{');

        int lastBrace =
                cleaned.lastIndexOf('}');

        if (firstBrace >= 0
                && lastBrace > firstBrace) {

            cleaned =
                    cleaned.substring(
                            firstBrace,
                            lastBrace + 1
                    );
        }

        return cleaned;
    }


    // =========================================================
    // RESULT VALIDATION
    // =========================================================

    private void validateGeneratedCoverLetter(
            CoverLetter coverLetter) {

        if (coverLetter == null) {

            throw new IllegalStateException(
                    "Generated cover letter is empty."
            );
        }

        if (coverLetter.getBody() == null
                || coverLetter.getBody().isBlank()) {

            throw new IllegalStateException(
                    "Generated cover letter body is empty."
            );
        }
    }


    // =========================================================
    // INPUT VALIDATION
    // =========================================================

    private void validateInput(
            String cvText,
            String jobDescription) {

        if (cvText == null
                || cvText.isBlank()) {

            throw new IllegalArgumentException(
                    "CV text cannot be empty."
            );
        }

        if (jobDescription == null
                || jobDescription.isBlank()) {

            throw new IllegalArgumentException(
                    "Job description cannot be empty."
            );
        }
    }


    // =========================================================
    // OPTIONAL VALUE
    // =========================================================

    private String normalizeOptional(
            String value,
            String fallback) {

        if (value == null
                || value.isBlank()) {

            return fallback;
        }

        return value.trim();
    }


    // =========================================================
    // TEXT LIMIT
    // =========================================================

    private String limitText(
            String text,
            int maxLength) {

        if (text == null) {

            return "";
        }

        String cleaned =
                text
                        .replace(
                                "\u0000",
                                ""
                        )
                        .trim();

        if (cleaned.length()
                <= maxLength) {

            return cleaned;
        }

        return cleaned.substring(
                0,
                maxLength
        );
    }
}