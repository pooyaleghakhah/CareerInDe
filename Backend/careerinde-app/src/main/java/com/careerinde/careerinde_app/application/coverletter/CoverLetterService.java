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

    private static final int MAX_CV_LENGTH = 6500;
    private static final int MAX_JOB_DESCRIPTION_LENGTH = 3000;


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

        validateInput(
                cvText,
                jobDescription
        );


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
        System.out.println(
                "=========================================="
        );
        System.out.println(
                "CAREERINDE COVER LETTER AI"
        );
        System.out.println(
                "Primary Provider: Gemini"
        );
        System.out.println(
                "Fallback Provider: Groq"
        );
        System.out.println(
                "=========================================="
        );


        String response;


        // =====================================================
        // PRIMARY PROVIDER: GEMINI
        // =====================================================

        try {

            long start =
                    System.currentTimeMillis();


            response =
                    geminiAIService.generateJson(
                            prompt,
                            schema,
                            0.15,
                            1800
                    );


            long duration =
                    System.currentTimeMillis()
                            - start;


            System.out.println();
            System.out.println(
                    "=========================================="
            );
            System.out.println(
                    "COVER LETTER PROVIDER: GEMINI"
            );
            System.out.println(
                    "Generation Time: "
                            + duration
                            + " ms"
            );
            System.out.println(
                    "=========================================="
            );


        } catch (Exception geminiException) {


            // =================================================
            // GEMINI FAILED -> GROQ FALLBACK
            // =================================================

            System.err.println();
            System.err.println(
                    "=========================================="
            );
            System.err.println(
                    "GEMINI COVER LETTER FAILED"
            );
            System.err.println(
                    "Reason: "
                            + geminiException.getMessage()
            );
            System.err.println(
                    "Switching to Groq fallback..."
            );
            System.err.println(
                    "=========================================="
            );


            try {

                long start =
                        System.currentTimeMillis();


                response =
                        groqService.sendPrompt(
                                prompt,
                                1200
                        );


                long duration =
                        System.currentTimeMillis()
                                - start;


                System.out.println();
                System.out.println(
                        "=========================================="
                );
                System.out.println(
                        "COVER LETTER PROVIDER: GROQ FALLBACK"
                );
                System.out.println(
                        "Generation Time: "
                                + duration
                                + " ms"
                );
                System.out.println(
                        "=========================================="
                );


            } catch (Exception groqException) {

                System.err.println();
                System.err.println(
                        "=========================================="
                );
                System.err.println(
                        "ALL COVER LETTER PROVIDERS FAILED"
                );
                System.err.println(
                        "Gemini: "
                                + geminiException.getMessage()
                );
                System.err.println(
                        "Groq: "
                                + groqException.getMessage()
                );
                System.err.println(
                        "=========================================="
                );


                throw new IllegalStateException(
                        "AI cover letter generation is temporarily unavailable.",
                        groqException
                );
            }
        }


        // =====================================================
        // PARSE FINAL RESPONSE
        // =====================================================

        return parseResponse(
                response
        );
    }


    // =========================================================
    // RESPONSE SCHEMA
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
    // PROFESSIONAL COVER LETTER PROMPT
    // =========================================================

    private String buildPrompt(
            String cvText,
            String jobDescription,
            String jobTitle,
            String companyName) {

        return """
You are CareerInDe's professional cover letter engine.

Write a personalized and recruiter-friendly cover letter
for the supplied job using ONLY verified facts from the CV.

Return ONLY valid JSON.

The JSON MUST use exactly this structure:

{
  "jobTitle": "...",
  "companyName": "...",
  "subject": "...",
  "greeting": "...",
  "body": "...",
  "closing": "...",
  "candidateName": "..."
}

FACTUAL SAFETY:

Never invent or assume:
- skills or technologies
- employers or job titles
- dates
- years of experience
- education or degree completion
- certifications
- projects
- responsibilities
- achievements
- metrics
- language proficiency
- company facts

The JOB DESCRIPTION is NOT evidence about the candidate.

Only claim a qualification when the CV supports it.

Do not transfer skills between contexts.

If Java, Spring Boot, AWS or another technology appears only
in a project or skills section, do not claim the candidate
used it at a specific employer unless the CV explicitly
supports that connection.

If a degree is still in progress, never describe the
candidate as already holding the completed degree.

If uncertain, omit the claim.


JOB MATCHING:

Internally identify the 3-5 most important job requirements.

Match them to the strongest verified evidence in the CV.

Use only supported evidence in the letter.

Do not output this analysis.


TARGET JOB:

Job Title:
%s

Company:
%s


PERSONALIZATION:

Make the letter clearly specific to this role.

Use company-specific facts only when explicitly supplied
in the job description.

Never invent company culture, products, projects,
technologies, values, strategy or achievements.


LANGUAGE:

Use the primary language of the job description.

For German:
- write natural professional German
- use formal "Sie"
- if no real contact person exists, use:
  "Sehr geehrte Damen und Herren,"
- normally close with:
  "Mit freundlichen Grüßen"

For English:
- write natural professional business English
- if no contact person exists, use:
  "Dear Hiring Team,"
- use a professional closing

Never invent a contact person.


CONTENT:

Write approximately 220-320 words.

Use 3-4 concise paragraphs.

Opening:
State the target role and strongest relevant profile.

Middle:
Connect important job requirements to VERIFIED CV evidence.

Closing:
Give credible role-specific motivation and invite further
discussion.

Do not simply repeat the CV.


WRITING QUALITY:

Sound natural, confident and credible.

Avoid:
- generic AI phrases
- clichés
- excessive enthusiasm
- buzzwords
- repetition
- unsupported claims
- keyword stuffing
- exaggerated seniority

Do not start every paragraph with "Ich" or "I".

Prefer concrete evidence over generic skill claims.


FINAL FACT CHECK:

Before returning the response, verify every candidate claim
against the CV.

Especially check:
- degree status
- technologies
- skill-to-experience attribution
- employers
- projects
- dates
- achievements
- metrics
- years of experience

Remove unsupported claims.

Verify company-specific statements against the supplied
job description.


OUTPUT RULES:

Return ONLY JSON.

No Markdown.
No ```json.
No code fences.
No explanations before JSON.
No explanations after JSON.

jobTitle:
Return exactly:
%s

companyName:
Return exactly:
%s

subject:
Professional application subject.

greeting:
Use a real contact person only when explicitly supplied.
Otherwise use a professional generic greeting.

body:
Only the main cover letter body.
Do not include greeting, closing or candidate name.

closing:
Professional closing phrase only.

candidateName:
Use the candidate name only if clearly identifiable
from the CV. Otherwise return an empty string.


================ CANDIDATE CV ================

%s


================ JOB DESCRIPTION ================

%s
"""
                .formatted(
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
                    cleanJson(
                            response
                    );


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


        if (cleaned.startsWith(
                "```json")) {

            cleaned =
                    cleaned
                            .substring(7)
                            .trim();

        } else if (
                cleaned.startsWith(
                        "```")) {

            cleaned =
                    cleaned
                            .substring(3)
                            .trim();
        }


        if (cleaned.endsWith(
                "```")) {

            cleaned =
                    cleaned
                            .substring(
                                    0,
                                    cleaned.length() - 3
                            )
                            .trim();
        }


        int firstBrace =
                cleaned.indexOf(
                        '{'
                );


        int lastBrace =
                cleaned.lastIndexOf(
                        '}'
                );


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