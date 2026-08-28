package com.careerinde.careerinde_app.resume.optimization;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OpenRouterResumeAIService {

    private static final String OPENROUTER_BASE_URL =
            "https://openrouter.ai/api/v1";

    /*
     * Fast free model.
     */
    private static final String MODEL =
            "nvidia/nemotron-3.5-lightning:free";

    private static final int MAX_RETRIES = 2;

    private static final int TIMEOUT_SECONDS = 90;

    private static final int MAX_CV_LENGTH = 6000;

    private static final int MAX_JOB_DESCRIPTION_LENGTH = 3000;


    @Value("${openrouter.api.key}")
    private String apiKey;


    private final WebClient webClient;

    private final ObjectMapper objectMapper;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public OpenRouterResumeAIService() {

        this.webClient =
                WebClient.builder()
                        .baseUrl(
                                OPENROUTER_BASE_URL
                        )
                        .defaultHeader(
                                HttpHeaders.CONTENT_TYPE,
                                MediaType.APPLICATION_JSON_VALUE
                        )
                        .build();


        this.objectMapper =
                new ObjectMapper();
    }


    // =========================================================
    // OPTIMIZE RESUME
    // =========================================================

    public String optimizeResume(
            String cvText,
            String jobDescription) {

        validateInput(
                cvText,
                jobDescription
        );


        String safeCv =
                limitText(
                        cvText,
                        MAX_CV_LENGTH
                );


        String safeJobDescription =
                limitText(
                        jobDescription,
                        MAX_JOB_DESCRIPTION_LENGTH
                );


        String systemPrompt = """
You are CareerInDe's professional ATS resume optimization engine.

Your task is to optimize an EXISTING resume for a specific job.

FACTUAL ACCURACY IS MANDATORY.

You MUST NOT invent information.

Never invent:
- skills
- technologies
- employers
- job titles
- dates
- universities
- degrees
- certifications
- languages
- projects
- responsibilities
- achievements
- percentages
- numbers
- metrics
- locations

If information does not exist in the original CV,
do not add it.

You may:
- improve wording
- improve clarity
- improve ATS relevance
- reorder existing information
- emphasize relevant qualifications
- rewrite existing bullet points professionally

IMPORTANT OUTPUT RULE:

Return ONLY valid JSON.

No Markdown.
No code fences.
No explanation.
No introduction.
No comments.
No text before the JSON.
No text after the JSON.

Your response MUST begin with:
{

Your response MUST end with:
}

All JSON property names must use double quotes.

All string values must use double quotes.

Do not create nested JSON strings.

Do NOT return something like:

{
  "{
  "fullName": "..."
}

That is invalid.

Return a normal JSON object.

ARRAY RULES:

skills:
array of strings

languages:
array of strings

certifications:
array of strings

experiences:
array of objects

education:
array of objects

projects:
array of objects

experience bulletPoints:
array of strings

project technologies:
array of strings

project bulletPoints:
array of strings

Use empty strings when scalar information is unavailable.

Use empty arrays when list information is unavailable.
""";


        String userPrompt = """
Optimize this resume for the target job.

Return exactly ONE JSON object using this structure:

{
  "fullName": "",
  "targetRole": "",
  "email": "",
  "phone": "",
  "location": "",
  "linkedin": "",
  "github": "",
  "professionalSummary": "",
  "skills": [],
  "experiences": [
    {
      "jobTitle": "",
      "company": "",
      "location": "",
      "startDate": "",
      "endDate": "",
      "bulletPoints": []
    }
  ],
  "education": [
    {
      "degree": "",
      "fieldOfStudy": "",
      "institution": "",
      "location": "",
      "startDate": "",
      "endDate": "",
      "grade": ""
    }
  ],
  "projects": [
    {
      "title": "",
      "description": "",
      "technologies": [],
      "bulletPoints": []
    }
  ],
  "languages": [],
  "certifications": [],
  "originalMatchScore": 0,
  "optimizedMatchScore": 0
}

STRICT TYPES:

fullName = string
targetRole = string
email = string
phone = string
location = string
linkedin = string
github = string
professionalSummary = string

skills = array of strings

experiences = array of objects

education = array of objects

projects = array of objects

languages = array of strings

certifications = array of strings

originalMatchScore = integer

optimizedMatchScore = integer


CONTENT LIMITS:

professionalSummary:
maximum 3 concise sentences.

skills:
maximum 15 items.

experiences:
maximum 4 relevant entries.

experience bulletPoints:
maximum 3 per experience.

Keep each experience bullet concise.

education:
maximum 3 entries.

projects:
maximum 3 entries.

project bulletPoints:
maximum 2 per project.

project technologies:
maximum 6 per project.

languages:
maximum 6 items.

certifications:
maximum 5 items.


FACTUAL SAFETY:

Do not copy requirements from the target job into the resume
unless those qualifications already exist in the original CV.

Do not invent metrics.

For example, do NOT write:
"improved performance by 40%"
unless 40% already exists in the original CV.

Do not invent responsibilities.

Do not invent employers.

Do not invent technologies.


SCORING:

originalMatchScore:
integer from 0 to 100.

optimizedMatchScore:
integer from 0 to 100.

The optimized score may improve because of better presentation,
keyword positioning and clearer wording.

It must NOT improve because you invented missing qualifications.


================ ORIGINAL CV ================

""" + safeCv + """


================ TARGET JOB DESCRIPTION ================

""" + safeJobDescription;


        // =====================================================
        // REQUEST BODY
        //
        // IMPORTANT:
        // NO response_format
        // NO json_schema
        //
        // Lightning returned HTTP 400 with the previous
        // structured-output request.
        // =====================================================

        Map<String, Object> requestBody =
                Map.of(
                        "model",
                        MODEL,

                        "messages",
                        List.of(

                                Map.of(
                                        "role",
                                        "system",
                                        "content",
                                        systemPrompt
                                ),

                                Map.of(
                                        "role",
                                        "user",
                                        "content",
                                        userPrompt
                                )
                        ),

                        "temperature",
                        0.0,

                        "max_tokens",
                        2400
                );


        return executeRequest(
                requestBody
        );
    }


    // =========================================================
    // EXECUTE REQUEST
    // =========================================================

    private String executeRequest(
            Map<String, Object> requestBody) {

        RuntimeException lastException =
                null;


        for (int attempt = 1;
             attempt <= MAX_RETRIES;
             attempt++) {

            try {

                System.out.println();
                System.out.println(
                        "======================================"
                );

                System.out.println(
                        "OPENROUTER RESUME REQUEST"
                );

                System.out.println(
                        "Attempt: "
                                + attempt
                                + "/"
                                + MAX_RETRIES
                );

                System.out.println(
                        "Model: "
                                + MODEL
                );

                System.out.println(
                        "Structured output: DISABLED"
                );

                System.out.println(
                        "======================================"
                );


                Map<?, ?> response =
                        webClient
                                .post()

                                .uri(
                                        "/chat/completions"
                                )

                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + apiKey
                                )

                                .header(
                                        "HTTP-Referer",
                                        "https://careerinde.com"
                                )

                                .header(
                                        "X-Title",
                                        "CareerInDe"
                                )

                                .bodyValue(
                                        requestBody
                                )

                                .retrieve()

                                .bodyToMono(
                                        Map.class
                                )

                                .block(
                                        Duration.ofSeconds(
                                                TIMEOUT_SECONDS
                                        )
                                );


                String finishReason =
                        extractFinishReason(
                                response
                        );


                System.out.println(
                        "OpenRouter finish_reason: "
                                + finishReason
                );


                String content =
                        extractContent(
                                response
                        );


                if (content == null ||
                        content.isBlank()) {

                    throw new RuntimeException(
                            "OpenRouter returned empty content."
                    );
                }


                System.out.println();
                System.out.println(
                        "===== RAW OPENROUTER CONTENT ====="
                );

                System.out.println(
                        content
                );

                System.out.println(
                        "=================================="
                );


                /*
                 * If the provider stopped because of
                 * token limit, do not accept the output.
                 */
                if ("length".equalsIgnoreCase(
                        finishReason)) {

                    throw new RuntimeException(
                            "OpenRouter response was truncated because of token limit."
                    );
                }


                String cleaned =
                        cleanJson(
                                content
                        );


                /*
                 * REAL JSON VALIDATION.
                 *
                 * We no longer only check whether the
                 * response begins with { and ends with }.
                 */
                JsonNode root =
                        parseJson(
                                cleaned
                        );


                validateResumeJson(
                        root
                );


                /*
                 * Normalize JSON formatting before
                 * sending it to ResumeOptimizationService.
                 */
                String normalizedJson =
                        objectMapper
                                .writeValueAsString(
                                        root
                                );


                System.out.println();
                System.out.println(
                        "===== VALID OPENROUTER JSON ====="
                );

                System.out.println(
                        normalizedJson
                );

                System.out.println(
                        "================================="
                );


                return normalizedJson;


            } catch (
                    WebClientResponseException exception) {


                System.err.println();
                System.err.println(
                        "===== OPENROUTER API ERROR ====="
                );

                System.err.println(
                        "Attempt: "
                                + attempt
                                + "/"
                                + MAX_RETRIES
                );

                System.err.println(
                        "Status: "
                                + exception.getStatusCode()
                );

                System.err.println(
                        "Response: "
                                + exception
                                .getResponseBodyAsString()
                );

                System.err.println(
                        "================================"
                );


                lastException =
                        new RuntimeException(
                                "OpenRouter API error: "
                                        + exception
                                        .getStatusCode(),
                                exception
                        );


                /*
                 * Most 4xx errors will not be fixed
                 * by retrying.
                 *
                 * 429 is the exception because it is
                 * a rate-limit error.
                 */
                if (exception
                        .getStatusCode()
                        .is4xxClientError()
                        &&
                        exception
                                .getStatusCode()
                                .value()
                                != 429) {

                    throw lastException;
                }


                if (attempt < MAX_RETRIES) {

                    sleep(
                            4000L * attempt
                    );
                }


            } catch (Exception exception) {


                System.err.println();
                System.err.println(
                        "===== OPENROUTER ERROR ====="
                );

                System.err.println(
                        "Attempt: "
                                + attempt
                                + "/"
                                + MAX_RETRIES
                );

                System.err.println(
                        "Reason: "
                                + exception.getMessage()
                );

                System.err.println(
                        "============================"
                );


                lastException =
                        new RuntimeException(
                                "OpenRouter request failed.",
                                exception
                        );


                if (attempt < MAX_RETRIES) {

                    System.err.println(
                            "Retrying OpenRouter..."
                    );


                    sleep(
                            3000L * attempt
                    );
                }
            }
        }


        throw new RuntimeException(
                "OpenRouter resume optimization failed.",
                lastException
        );
    }


    // =========================================================
    // EXTRACT FINISH REASON
    // =========================================================

    private String extractFinishReason(
            Map<?, ?> response) {


        if (response == null) {

            return "unknown";
        }


        Object choicesObject =
                response.get(
                        "choices"
                );


        if (!(choicesObject
                instanceof List<?> choices)
                ||
                choices.isEmpty()) {

            return "unknown";
        }


        Object firstChoiceObject =
                choices.get(0);


        if (!(firstChoiceObject
                instanceof Map<?, ?> firstChoice)) {

            return "unknown";
        }


        Object finishReason =
                firstChoice.get(
                        "finish_reason"
                );


        if (finishReason == null) {

            return "unknown";
        }


        return finishReason
                .toString();
    }


    // =========================================================
    // EXTRACT CONTENT
    // =========================================================

    private String extractContent(
            Map<?, ?> response) {


        if (response == null) {

            throw new RuntimeException(
                    "No response received from OpenRouter."
            );
        }


        Object choicesObject =
                response.get(
                        "choices"
                );


        if (!(choicesObject
                instanceof List<?> choices)
                ||
                choices.isEmpty()) {

            throw new RuntimeException(
                    "OpenRouter returned no choices."
            );
        }


        Object firstChoiceObject =
                choices.get(0);


        if (!(firstChoiceObject
                instanceof Map<?, ?> firstChoice)) {

            throw new RuntimeException(
                    "Invalid OpenRouter choice."
            );
        }


        Object messageObject =
                firstChoice.get(
                        "message"
                );


        if (!(messageObject
                instanceof Map<?, ?> message)) {

            throw new RuntimeException(
                    "OpenRouter returned no message."
            );
        }


        Object contentObject =
                message.get(
                        "content"
                );


        if (contentObject == null) {

            throw new RuntimeException(
                    "OpenRouter returned no content."
            );
        }


        /*
         * Standard OpenRouter text response.
         */
        if (contentObject
                instanceof String content) {

            return content.trim();
        }


        /*
         * Some providers may return content parts.
         */
        if (contentObject
                instanceof List<?> contentParts) {


            StringBuilder builder =
                    new StringBuilder();


            for (Object partObject :
                    contentParts) {


                if (partObject
                        instanceof Map<?, ?> part) {


                    Object text =
                            part.get(
                                    "text"
                            );


                    if (text != null) {

                        builder.append(
                                text
                        );
                    }
                }
            }


            String result =
                    builder
                            .toString()
                            .trim();


            if (!result.isBlank()) {

                return result;
            }
        }


        return contentObject
                .toString()
                .trim();
    }


    // =========================================================
    // CLEAN JSON
    // =========================================================

    private String cleanJson(
            String response) {


        if (response == null ||
                response.isBlank()) {

            return "";
        }


        String cleaned =
                response.trim();


        // -----------------------------------------------------
        // REMOVE MARKDOWN
        // -----------------------------------------------------

        if (cleaned.startsWith(
                "```json")) {

            cleaned =
                    cleaned.substring(7)
                            .trim();

        } else if (
                cleaned.startsWith(
                        "```")) {

            cleaned =
                    cleaned.substring(3)
                            .trim();
        }


        if (cleaned.endsWith(
                "```")) {

            cleaned =
                    cleaned.substring(
                            0,
                            cleaned.length() - 3
                    ).trim();
        }


        /*
         * Find the first real JSON object.
         */
        int firstBrace =
                cleaned.indexOf('{');


        if (firstBrace < 0) {

            throw new RuntimeException(
                    "AI response does not contain a JSON object."
            );
        }


        cleaned =
                cleaned.substring(
                        firstBrace
                ).trim();


        /*
         * Fix the specific malformed prefix previously
         * observed from Nemotron:
         *
         * {
         *   "{
         *   "field": ...
         *
         * We do NOT perform broad arbitrary JSON repair.
         */
        if (cleaned.matches(
                "(?s)^\\{\\s*\"\\{\\s*\".*")) {


            int badPrefix =
                    cleaned.indexOf(
                            "\"{"
                    );


            if (badPrefix >= 0) {

                String afterBadPrefix =
                        cleaned.substring(
                                badPrefix + 2
                        ).trim();


                cleaned =
                        "{"
                                + afterBadPrefix;
            }
        }


        /*
         * Only remove surrounding text.
         * Do not invent missing braces.
         */
        int lastBrace =
                cleaned.lastIndexOf('}');


        if (lastBrace < 0) {

            throw new RuntimeException(
                    "AI returned incomplete JSON."
            );
        }


        cleaned =
                cleaned.substring(
                        0,
                        lastBrace + 1
                ).trim();


        return cleaned;
    }


    // =========================================================
    // PARSE JSON
    // =========================================================

    private JsonNode parseJson(
            String json) {


        if (json == null ||
                json.isBlank()) {

            throw new RuntimeException(
                    "AI returned empty JSON."
            );
        }


        try {

            JsonNode root =
                    objectMapper
                            .readTree(
                                    json
                            );


            if (root == null ||
                    !root.isObject()) {

                throw new RuntimeException(
                        "AI response is not a JSON object."
                );
            }


            return root;


        } catch (Exception exception) {

            throw new RuntimeException(
                    "AI returned malformed JSON.",
                    exception
            );
        }
    }


    // =========================================================
    // VALIDATE RESUME JSON
    // =========================================================

    private void validateResumeJson(
            JsonNode root) {


        requireTextField(
                root,
                "fullName"
        );


        requireTextField(
                root,
                "targetRole"
        );


        requireTextField(
                root,
                "email"
        );


        requireTextField(
                root,
                "phone"
        );


        requireTextField(
                root,
                "location"
        );


        requireTextField(
                root,
                "linkedin"
        );


        requireTextField(
                root,
                "github"
        );


        requireTextField(
                root,
                "professionalSummary"
        );


        requireArrayField(
                root,
                "skills"
        );


        requireArrayField(
                root,
                "experiences"
        );


        requireArrayField(
                root,
                "education"
        );


        requireArrayField(
                root,
                "projects"
        );


        requireArrayField(
                root,
                "languages"
        );


        requireArrayField(
                root,
                "certifications"
        );


        requireIntegerField(
                root,
                "originalMatchScore"
        );


        requireIntegerField(
                root,
                "optimizedMatchScore"
        );


        validateStringArray(
                root.get("skills"),
                "skills"
        );


        validateStringArray(
                root.get("languages"),
                "languages"
        );


        validateStringArray(
                root.get("certifications"),
                "certifications"
        );


        validateExperiences(
                root.get("experiences")
        );


        validateEducation(
                root.get("education")
        );


        validateProjects(
                root.get("projects")
        );


        validateScore(
                root.get("originalMatchScore"),
                "originalMatchScore"
        );


        validateScore(
                root.get("optimizedMatchScore"),
                "optimizedMatchScore"
        );
    }


    // =========================================================
    // EXPERIENCE VALIDATION
    // =========================================================

    private void validateExperiences(
            JsonNode experiences) {


        for (JsonNode experience :
                experiences) {


            if (!experience.isObject()) {

                throw new RuntimeException(
                        "experiences must contain objects."
                );
            }


            requireTextField(
                    experience,
                    "jobTitle"
            );


            requireTextField(
                    experience,
                    "company"
            );


            requireTextField(
                    experience,
                    "location"
            );


            requireTextField(
                    experience,
                    "startDate"
            );


            requireTextField(
                    experience,
                    "endDate"
            );


            requireArrayField(
                    experience,
                    "bulletPoints"
            );


            validateStringArray(
                    experience.get(
                            "bulletPoints"
                    ),
                    "experience bulletPoints"
            );
        }
    }


    // =========================================================
    // EDUCATION VALIDATION
    // =========================================================

    private void validateEducation(
            JsonNode education) {


        for (JsonNode item :
                education) {


            if (!item.isObject()) {

                throw new RuntimeException(
                        "education must contain objects."
                );
            }


            requireTextField(
                    item,
                    "degree"
            );


            requireTextField(
                    item,
                    "fieldOfStudy"
            );


            requireTextField(
                    item,
                    "institution"
            );


            requireTextField(
                    item,
                    "location"
            );


            requireTextField(
                    item,
                    "startDate"
            );


            requireTextField(
                    item,
                    "endDate"
            );


            requireTextField(
                    item,
                    "grade"
            );
        }
    }


    // =========================================================
    // PROJECT VALIDATION
    // =========================================================

    private void validateProjects(
            JsonNode projects) {


        for (JsonNode project :
                projects) {


            if (!project.isObject()) {

                throw new RuntimeException(
                        "projects must contain objects."
                );
            }


            requireTextField(
                    project,
                    "title"
            );


            requireTextField(
                    project,
                    "description"
            );


            requireArrayField(
                    project,
                    "technologies"
            );


            requireArrayField(
                    project,
                    "bulletPoints"
            );


            validateStringArray(
                    project.get(
                            "technologies"
                    ),
                    "project technologies"
            );


            validateStringArray(
                    project.get(
                            "bulletPoints"
                    ),
                    "project bulletPoints"
            );
        }
    }


    // =========================================================
    // REQUIRED TEXT FIELD
    // =========================================================

    private void requireTextField(
            JsonNode node,
            String fieldName) {


        if (!node.has(fieldName)) {

            throw new RuntimeException(
                    "AI JSON missing field: "
                            + fieldName
            );
        }


        JsonNode field =
                node.get(
                        fieldName
                );


        if (field == null ||
                !field.isTextual()) {

            throw new RuntimeException(
                    "AI JSON field must be string: "
                            + fieldName
            );
        }
    }


    // =========================================================
    // REQUIRED ARRAY FIELD
    // =========================================================

    private void requireArrayField(
            JsonNode node,
            String fieldName) {


        if (!node.has(fieldName)) {

            throw new RuntimeException(
                    "AI JSON missing field: "
                            + fieldName
            );
        }


        JsonNode field =
                node.get(
                        fieldName
                );


        if (field == null ||
                !field.isArray()) {

            throw new RuntimeException(
                    "AI JSON field must be array: "
                            + fieldName
            );
        }
    }


    // =========================================================
    // REQUIRED INTEGER
    // =========================================================

    private void requireIntegerField(
            JsonNode node,
            String fieldName) {


        if (!node.has(fieldName)) {

            throw new RuntimeException(
                    "AI JSON missing field: "
                            + fieldName
            );
        }


        JsonNode field =
                node.get(
                        fieldName
                );


        if (field == null ||
                !field.isIntegralNumber()) {

            throw new RuntimeException(
                    "AI JSON field must be integer: "
                            + fieldName
            );
        }
    }


    // =========================================================
    // STRING ARRAY VALIDATION
    // =========================================================

    private void validateStringArray(
            JsonNode array,
            String fieldName) {


        if (array == null ||
                !array.isArray()) {

            throw new RuntimeException(
                    fieldName
                            + " must be an array."
            );
        }


        for (JsonNode value :
                array) {


            if (!value.isTextual()) {

                throw new RuntimeException(
                        fieldName
                                + " must contain only strings."
                );
            }
        }
    }


    // =========================================================
    // SCORE VALIDATION
    // =========================================================

    private void validateScore(
            JsonNode scoreNode,
            String fieldName) {


        int score =
                scoreNode.asInt();


        if (score < 0 ||
                score > 100) {

            throw new RuntimeException(
                    fieldName
                            + " must be between 0 and 100."
            );
        }
    }


    // =========================================================
    // INPUT VALIDATION
    // =========================================================

    private void validateInput(
            String cvText,
            String jobDescription) {


        if (cvText == null ||
                cvText.isBlank()) {

            throw new IllegalArgumentException(
                    "CV text cannot be empty."
            );
        }


        if (jobDescription == null ||
                jobDescription.isBlank()) {

            throw new IllegalArgumentException(
                    "Job description cannot be empty."
            );
        }
    }


    // =========================================================
    // LIMIT INPUT
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


    // =========================================================
    // RETRY WAIT
    // =========================================================

    private void sleep(
            long milliseconds) {


        try {

            Thread.sleep(
                    milliseconds
            );


        } catch (
                InterruptedException exception) {


            Thread.currentThread()
                    .interrupt();


            throw new RuntimeException(
                    "OpenRouter retry interrupted.",
                    exception
            );
        }
    }
}