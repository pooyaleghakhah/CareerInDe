package com.careerinde.careerinde_app.application.coverletter;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.careerinde.careerinde_app.ai.gemini.GeminiAIService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CoverLetterService {

    private final GeminiAIService geminiAIService;
    private final ObjectMapper objectMapper;

    private static final int MAX_CV_LENGTH = 6500;
    private static final int MAX_JOB_DESCRIPTION_LENGTH = 3000;


    public CoverLetterService(
            GeminiAIService geminiAIService,
            ObjectMapper objectMapper) {

        this.geminiAIService = geminiAIService;
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
                "Compact Professional Prompt: ENABLED"
        );
        System.out.println(
                "Model: " + geminiAIService.getModel()
        );
        System.out.println(
                "=========================================="
        );


        String response =
                geminiAIService.generateJson(
                        prompt,
                        schema,
                        0.15,
                        1800
                );

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
    // COMPACT PROFESSIONAL COVER LETTER PROMPT
    // =========================================================

    private String buildPrompt(
            String cvText,
            String jobDescription,
            String jobTitle,
            String companyName) {

        return """
You are CareerInDe's professional cover letter engine.

Write a personalized, recruiter-friendly cover letter for
the supplied job using ONLY verified facts from the CV.


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

The JOB DESCRIPTION is not evidence about the candidate.

Only claim a qualification when the CV supports it.

Do not transfer skills between contexts.

Example:
If Java or Spring Boot appears only in a project or skills
section, do not claim the candidate used it at a specific
employer unless the CV explicitly says so.

If a degree is still in progress, never describe the
candidate as already holding that completed degree.

If uncertain, omit the claim.


JOB MATCHING:

Internally identify the 3-5 most important job requirements.

Match them to the strongest verified evidence in the CV.

Use supported evidence to build the letter.

Do not mention unsupported requirements simply because
they appear in the job advertisement.

Do not output the analysis.


PERSONALIZATION:

Target Job:
%s

Company:
%s

Make the letter clearly specific to this role.

Use company-specific facts only when supplied in the job
description.

Never invent company culture, products, technologies,
projects, values or strategy.


LANGUAGE:

Use the primary language of the job description.

For German:
- use natural professional German
- use "Sie"
- if no real contact person exists, use:
  "Sehr geehrte Damen und Herren,"
- normally close with:
  "Mit freundlichen Grüßen"

For English:
use natural professional business English.

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

Check especially:
- degree status
- technologies
- skill-to-experience attribution
- employers
- projects
- dates
- achievements
- metrics
- years of experience

Remove anything that cannot be supported.

Verify company-specific statements against the supplied
job description.


OUTPUT:

Return ONLY JSON matching the provided schema.

jobTitle:
Return the supplied job title.

companyName:
Return the supplied company name.

subject:
Professional application subject.

greeting:
Use a real contact person only if supplied.
Otherwise use a professional generic greeting.

body:
Only the main cover letter body.
Do not repeat greeting, closing or candidate name.

closing:
Professional closing phrase only.

candidateName:
Use the name only if clearly identifiable from the CV.
Otherwise return an empty string.

No markdown.
No explanations.


================ CANDIDATE CV ================

%s


================ JOB DESCRIPTION ================

%s
"""
                .formatted(
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
                    "Gemini returned an empty cover letter."
            );
        }

        try {

            CoverLetter coverLetter =
                    objectMapper.readValue(
                            response,
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

        if (cleaned.length() <= maxLength) {
            return cleaned;
        }

        return cleaned.substring(
                0,
                maxLength
        );
    }
}