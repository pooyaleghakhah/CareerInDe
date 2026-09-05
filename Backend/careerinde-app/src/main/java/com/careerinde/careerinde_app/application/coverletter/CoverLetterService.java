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
                        cvText,
                        jobDescription,
                        safeJobTitle,
                        safeCompanyName
                );

        Map<String, Object> schema =
                buildResponseSchema();

        String response =
                geminiAIService.generateJson(
                        prompt,
                        schema,
                        0.2,
                        3000
                );

        return parseResponse(
                response
        );
    }


    // =========================================================
    // GEMINI RESPONSE SCHEMA
    // =========================================================

    private Map<String, Object> buildResponseSchema() {

        Map<String, Object> properties =
                Map.of(
                        "jobTitle",
                        Map.of(
                                "type",
                                "STRING"
                        ),

                        "companyName",
                        Map.of(
                                "type",
                                "STRING"
                        ),

                        "subject",
                        Map.of(
                                "type",
                                "STRING"
                        ),

                        "greeting",
                        Map.of(
                                "type",
                                "STRING"
                        ),

                        "body",
                        Map.of(
                                "type",
                                "STRING"
                        ),

                        "closing",
                        Map.of(
                                "type",
                                "STRING"
                        ),

                        "candidateName",
                        Map.of(
                                "type",
                                "STRING"
                        )
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
    // PROMPT
    // =========================================================

    private String buildPrompt(
            String cvText,
            String jobDescription,
            String jobTitle,
            String companyName) {

        return """
                You are the CareerInDe AI Cover Letter Assistant.

                Create a professional and personalized cover letter
                for the supplied job.

                ==================================================
                STRICT FACTUAL RULES
                ==================================================

                Use ONLY facts supported by the candidate CV.

                Never invent:
                - employers
                - job titles
                - employment dates
                - years of experience
                - education
                - certifications
                - technologies
                - skills
                - achievements
                - projects
                - numerical metrics

                You may:
                - rephrase existing CV information
                - emphasize relevant experience
                - connect verified CV experience to job requirements

                If the job requires something that is not supported
                by the CV, DO NOT claim that the candidate has it.

                ==================================================
                WRITING STYLE
                ==================================================

                - Professional
                - Natural
                - Specific to this job
                - Approximately 250-350 words
                - Avoid generic AI phrases
                - Avoid exaggerated enthusiasm
                - Do not repeat the entire CV
                - Focus on the strongest relevant qualifications
                - Use the same primary language as the job description

                If no contact person is available, use an appropriate
                professional generic greeting.

                ==================================================
                JOB
                ==================================================

                Job Title:
                %s

                Company:
                %s

                ==================================================
                CANDIDATE CV
                ==================================================

                %s

                ==================================================
                JOB DESCRIPTION
                ==================================================

                %s

                ==================================================
                OUTPUT RULES
                ==================================================

                Return the requested structured JSON fields.

                subject:
                A concise professional application subject.

                greeting:
                Professional greeting.

                body:
                The complete main body of the cover letter.

                closing:
                Professional closing such as "Sincerely," or the
                appropriate equivalent in the job description language.

                candidateName:
                Use the candidate's name ONLY if clearly identifiable
                from the CV. Otherwise return an empty string.

                jobTitle:
                Use the supplied job title.

                companyName:
                Use the supplied company name.
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
}