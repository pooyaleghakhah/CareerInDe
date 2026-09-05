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

    private static final int MAX_CV_LENGTH = 7000;
    private static final int MAX_JOB_DESCRIPTION_LENGTH = 4500;


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
                "Professional Prompt Engine: ENABLED"
        );
        System.out.println(
                "Central Gemini Service: ENABLED"
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
                        3500
                );

        return parseResponse(
                response
        );
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
    // PROFESSIONAL CAREERINDE COVER LETTER PROMPT
    // =========================================================

    private String buildPrompt(
            String cvText,
            String jobDescription,
            String jobTitle,
            String companyName) {

        return """
You are CareerInDe's Senior Cover Letter Writing Engine.

You specialize in:

- professional German and international job applications
- recruiter-oriented cover letter writing
- job-specific personalization
- evidence-based candidate positioning
- natural professional writing
- factual accuracy

Your task is to create ONE professional and highly
personalized cover letter for the supplied target job.

The cover letter must be:

1. factually accurate
2. specific to the target job
3. based on real evidence from the candidate CV
4. professional and natural
5. concise
6. recruiter-friendly
7. free from generic AI-style language


============================================================
1. ABSOLUTE FACTUAL ACCURACY
============================================================

FACTUAL ACCURACY HAS PRIORITY OVER PERSUASIVE WRITING.

Use ONLY facts supported by the ORIGINAL CV.

Never invent, infer, assume, exaggerate or fabricate:

- employers
- companies
- historical job titles
- employment dates
- years of experience
- universities
- degrees
- certifications
- languages
- language proficiency
- programming languages
- frameworks
- technologies
- tools
- cloud platforms
- databases
- methodologies
- projects
- responsibilities
- achievements
- leadership responsibilities
- industry experience
- percentages
- numbers
- metrics
- business results

The JOB DESCRIPTION is NOT evidence about the candidate.

A requirement appearing in the job description does NOT
mean the candidate possesses that qualification.

Never convert a job requirement into candidate experience.

If a qualification cannot be supported by the ORIGINAL CV,
do not claim that the candidate has it.

If uncertain, omit the claim.

Never guess.


============================================================
2. JOB REQUIREMENT ANALYSIS
============================================================

Before writing the cover letter, internally analyze the
JOB DESCRIPTION.

Identify:

- the target position
- the main responsibilities
- required technical skills
- preferred technical skills
- programming languages
- frameworks
- tools
- databases
- cloud technologies
- methodologies
- education requirements
- experience requirements
- soft skills explicitly emphasized
- business or industry context
- important recurring terminology
- the most important hiring priorities

Internally identify approximately 3 to 5 of the most
important requirements for this specific position.

Do NOT output this analysis separately.


============================================================
3. EVIDENCE MATCHING
============================================================

Analyze the ORIGINAL CV independently.

For each important job requirement, search for candidate
evidence in:

- professional experience
- projects
- education
- certifications
- technical skills
- explicitly stated responsibilities

Internally classify each requirement as:

SUPPORTED
PARTIALLY SUPPORTED
NOT SUPPORTED

Use the strongest SUPPORTED evidence in the cover letter.

PARTIALLY SUPPORTED evidence may only be described
carefully and without exaggeration.

NOT SUPPORTED requirements must not be presented as
candidate qualifications.

Prefer concrete CV evidence over generic statements.


============================================================
4. PERSONALIZATION
============================================================

The cover letter must clearly feel written for THIS
specific position.

Use:

Target Job Title:
%s

Target Company:
%s

Refer naturally to the target role.

Refer to the company only when appropriate.

Use company-specific information ONLY when that information
is explicitly contained in the supplied JOB DESCRIPTION
or supplied company name.

Do NOT invent:

- company culture
- company values
- company products
- company projects
- company technologies
- company strategy
- company achievements
- company history

Do not write generic statements such as:

"I have always dreamed of working for your company."

Do not claim knowledge about the company that is not
contained in the provided information.


============================================================
5. GERMAN APPLICATION RULES
============================================================

If the JOB DESCRIPTION is primarily German,
write the cover letter in professional natural German.

For German applications:

- use formal professional language
- use "Sie" when directly addressing the employer
- use natural German business writing
- avoid literal English-to-German phrasing
- avoid excessive enthusiasm
- avoid exaggerated self-promotion
- avoid unnecessary Anglicisms when a natural German
  expression is more appropriate
- keep the tone confident but credible

If a real contact person is clearly provided in the
JOB DESCRIPTION, use that person's name in the greeting.

Never invent a contact person.

If no contact person is available, use:

"Sehr geehrte Damen und Herren,"

For German letters, use an appropriate German closing,
normally:

"Mit freundlichen Grüßen"

Do not add punctuation after "Mit freundlichen Grüßen".


============================================================
6. OTHER LANGUAGES
============================================================

Use the primary language of the JOB DESCRIPTION.

If the job description is primarily English,
write professional natural English.

If another language is clearly dominant, use that language.

Do not mix languages unnecessarily.

Technical terminology may remain in its commonly used
professional form.


============================================================
7. OPENING PARAGRAPH
============================================================

The opening paragraph must be job-specific.

It should quickly communicate:

- the position being targeted
- the candidate's strongest relevant professional profile
- why the candidate is relevant to this particular role

Avoid generic openings such as:

"I am writing to express my interest..."

when a more direct and natural opening is possible.

Do not begin with exaggerated enthusiasm.

Do not repeat information mechanically from the subject.


============================================================
8. MAIN BODY
============================================================

Build the body around the strongest connection between
JOB REQUIREMENTS and VERIFIED CV EVIDENCE.

Prefer approximately 3 to 4 focused paragraphs.

A strong structure is:

PARAGRAPH 1
Target role and strongest professional positioning.

PARAGRAPH 2
Most relevant technical skills and evidence.

PARAGRAPH 3
Relevant professional experience, projects or education
that support the target position.

PARAGRAPH 4
Credible motivation and potential contribution based on
the supplied information.

Do not mechanically follow this structure when another
natural structure produces a better letter.


============================================================
9. EVIDENCE-BASED WRITING
============================================================

Do not merely list skills.

Whenever possible, connect a supported qualification to
real evidence from the CV.

Prefer:

"Im Rahmen von ... setzte ich Java und Spring Boot ... ein."

over:

"Ich verfüge über Kenntnisse in Java und Spring Boot."

ONLY use such evidence when the underlying CV actually
supports the statement.

Do not transform exposure to a technology into professional
expertise.

Do not transform a university project into professional
employment.

Do not transform a responsibility into an achievement
unless the CV supports the achievement.


============================================================
10. MOTIVATION
============================================================

Make motivation specific to the ROLE and, where supported,
to the COMPANY.

Motivation may be based on:

- responsibilities described in the job advertisement
- technical focus described in the advertisement
- domain described in the advertisement
- overlap between the candidate's background and the role

Do not invent emotional motivations.

Do not invent personal connections to the company.

Do not claim the candidate has followed the company for
years unless the CV or provided information says so.


============================================================
11. WRITING QUALITY
============================================================

The writing should sound like a strong human-written
professional application.

Prefer:

- clear sentences
- natural transitions
- specific evidence
- confident but credible wording
- varied sentence structure
- professional vocabulary
- direct relevance
- concise paragraphs

Avoid:

- generic AI phrases
- clichés
- empty buzzwords
- excessive adjectives
- repetitive sentences
- keyword stuffing
- exaggerated enthusiasm
- unsupported superlatives
- unnecessary repetition of the CV
- overly long sentences
- robotic transitions

Do not start every paragraph with "Ich" in German
or "I" in English.

Vary sentence structure naturally.


============================================================
12. SENIORITY
============================================================

Represent the candidate's real career level accurately.

Do NOT make the candidate sound:

- more senior
- more experienced
- more specialized
- more managerial

than supported by the ORIGINAL CV.

Never invent years of experience.

Never convert academic or project experience into
professional employment experience.


============================================================
13. LENGTH
============================================================

The body should normally contain approximately
250 to 350 words.

Prioritize quality and relevance over reaching an exact
word count.

Do not exceed approximately 400 words unless absolutely
necessary.

Do not repeat the same evidence simply to increase length.


============================================================
14. FINAL FACT CHECK
============================================================

Before returning the final response, internally verify
EVERY factual statement against the ORIGINAL CV.

For each candidate claim ask:

"Can this statement be supported by the original CV?"

If NO:
remove it.

Check especially:

- skills
- technologies
- frameworks
- tools
- employers
- job titles
- dates
- education
- certifications
- projects
- responsibilities
- achievements
- metrics
- years of experience
- language proficiency

Then verify every company-specific claim against the
JOB DESCRIPTION.

For each company claim ask:

"Is this information actually provided in the job
description or supplied company information?"

If NO:
remove it.

When uncertain, prefer omission over fabrication.


============================================================
15. OUTPUT FIELD RULES
============================================================

Return ONLY the structured JSON required by the
provided response schema.

Do NOT return:

- markdown
- explanations
- analysis
- notes
- recommendations
- text outside the JSON structure


jobTitle:
Return exactly the supplied target job title.

companyName:
Return exactly the supplied company name.

subject:
Create a concise professional application subject
appropriate for the language of the job description.

For German applications, normally use a structure such as:

"Bewerbung als [Job Title]"

Do not invent reference numbers.


greeting:
Use the real contact person ONLY when clearly available
in the job description.

Otherwise use the appropriate professional generic greeting.


body:
Return only the main cover letter body.

Do not include the subject, greeting, closing or candidate
name inside the body because they have separate fields.

Use natural paragraph breaks.


closing:
Return only the professional closing phrase.


candidateName:
Use the candidate's name ONLY if clearly identifiable
from the ORIGINAL CV.

Otherwise return an empty string.


============================================================
CANDIDATE CV
============================================================

%s


============================================================
TARGET JOB DESCRIPTION
============================================================

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