package com.careerinde.careerinde_app.ai;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.careerinde.careerinde_app.AIAnalysisResult.AIAnalysisResult;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private static final String MODEL =
            "openai/gpt-oss-20b";

    private static final int MAX_RETRIES = 3;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public OpenAIService() {

        this.objectMapper =
                new ObjectMapper();

        this.webClient =
                WebClient.builder()
                        .baseUrl(
                                "https://api.groq.com/openai/v1"
                        )
                        .defaultHeader(
                                HttpHeaders.CONTENT_TYPE,
                                MediaType.APPLICATION_JSON_VALUE
                        )
                        .build();
    }


    // =========================================================
    // CV ANALYSIS
    // =========================================================

    public AIAnalysisResult analyzeCV(
            String cvText) {

        if (cvText == null ||
                cvText.isBlank()) {

            return createFallbackResult();
        }


        String safeCvText =
                limitText(
                        cvText,
                        7000
                );


        String prompt = """
You are an advanced ATS system and career advisor
specialized in the German technology job market.

Analyze the candidate CV below realistically.

Evaluate:

- technical skills
- professional experience
- education
- academic projects
- personal projects
- transferable skills
- international experience
- language skills
- German job market readiness

Return ONLY valid JSON.

Do not use Markdown.
Do not use ```json.
Do not use code fences.
Do not write anything before the JSON.
Do not write anything after the JSON.

Use exactly this structure:

{
  "atsScore": 78,
  "profileLevel": "Strong",
  "bestJobMatch": "Java Backend Developer",
  "strengths": [
    "strength 1",
    "strength 2",
    "strength 3"
  ],
  "missingSkills": [
    "skill 1",
    "skill 2"
  ],
  "recommendations": [
    "recommendation 1",
    "recommendation 2",
    "recommendation 3"
  ]
}

Rules:

atsScore:
Integer from 0 to 100.

profileLevel must be exactly one of:

Needs Improvement
Developing
Good
Strong
Excellent

bestJobMatch:
Return ONE realistic job role.

strengths:
Return 3 to 5 concise strengths.

missingSkills:
Return 0 to 5 relevant missing skills.

recommendations:
Return 3 to 5 concise recommendations.

IMPORTANT:

Never invent:
- work experience
- education
- employers
- certifications
- skills
- achievements
- projects

Only use information supported by the CV.

CV:

""" + safeCvText;


        Map<String, Object> requestBody =
                Map.of(
                        "model",
                        MODEL,

                        "messages",
                        new Object[]{

                                Map.of(
                                        "role",
                                        "system",
                                        "content",
                                        """
You are an ATS resume analysis engine.

Always return valid JSON only.

Never use Markdown.
Never use code fences.
Never invent candidate information.
"""
                                ),

                                Map.of(
                                        "role",
                                        "user",
                                        "content",
                                        prompt
                                )
                        },

                        "temperature",
                        0.1,

                        "max_tokens",
                        1200
                );


        try {

            String content =
                    executeRequestWithRetry(
                            requestBody
                    );


            System.out.println();
            System.out.println(
                    "===== GROQ RAW CV ANALYSIS ====="
            );

            System.out.println(
                    content
            );

            System.out.println(
                    "================================"
            );


            String cleanedJson =
                    cleanJson(
                            content
                    );


            AIAnalysisResult result =
                    objectMapper.readValue(
                            cleanedJson,
                            AIAnalysisResult.class
                    );


            // =========================================
            // ATS SCORE SAFETY
            // =========================================

            if (result.getAtsScore() < 0) {

                result.setAtsScore(
                        0
                );
            }


            if (result.getAtsScore() > 100) {

                result.setAtsScore(
                        100
                );
            }


            // =========================================
            // NULL SAFETY
            // =========================================

            if (result.getProfileLevel() == null) {

                result.setProfileLevel(
                        "Good"
                );
            }


            if (result.getBestJobMatch() == null) {

                result.setBestJobMatch(
                        "Technology Professional"
                );
            }


            if (result.getStrengths() == null) {

                result.setStrengths(
                        List.of()
                );
            }


            if (result.getMissingSkills() == null) {

                result.setMissingSkills(
                        List.of()
                );
            }


            if (result.getRecommendations() == null) {

                result.setRecommendations(
                        List.of()
                );
            }


            return result;


        } catch (Exception exception) {

            System.err.println();
            System.err.println(
                    "===== AI ANALYSIS ERROR ====="
            );

            System.err.println(
                    exception.getMessage()
            );

            exception.printStackTrace();

            System.err.println(
                    "============================="
            );


            return createFallbackResult();
        }
    }


    // =========================================================
    // GENERIC AI PROMPT
    //
    // Used by ResumeOptimizationService
    // =========================================================

    public String sendPrompt(
            String prompt) {

        if (prompt == null ||
                prompt.isBlank()) {

            throw new IllegalArgumentException(
                    "AI prompt cannot be empty."
            );
        }


        /*
         * IMPORTANT:
         *
         * We intentionally DO NOT use:
         *
         * response_format = json_object
         *
         * because Groq returned:
         *
         * json_validate_failed
         *
         * with the current model.
         *
         * JSON output is instead enforced
         * through the system prompt.
         */


        Map<String, Object> requestBody =
                Map.of(
                        "model",
                        MODEL,

                        "messages",
                        new Object[]{

                                Map.of(
                                        "role",
                                        "system",
                                        "content",
                                        """
You are CareerInDe's resume optimization engine.

Your job is to optimize resumes for specific
job descriptions.

IMPORTANT OUTPUT RULES:

Return ONLY valid JSON.

Do not use Markdown.

Do not use code fences.

Do not write ```json.

Do not include explanations before JSON.

Do not include explanations after JSON.

The first character of your response
must be {

The last character of your response
must be }

Never invent candidate information.

Never invent:

- employers
- job titles
- dates
- education
- degrees
- certifications
- technologies
- projects
- achievements
- metrics
- languages
- responsibilities

You may improve wording and structure,
but every factual statement must be
supported by the original CV.

Write professional,
ATS-friendly resume content.
"""
                                ),

                                Map.of(
                                        "role",
                                        "user",
                                        "content",
                                        prompt
                                )
                        },

                        "temperature",
                        0.1,

                        "max_tokens",
                        1600
                );


        try {

            String content =
                    executeRequestWithRetry(
                            requestBody
                    );


            if (content == null ||
                    content.isBlank()) {

                throw new RuntimeException(
                        "AI returned an empty response."
                );
            }


            System.out.println();
            System.out.println(
                    "===== GROQ RAW PROMPT RESPONSE ====="
            );

            System.out.println(
                    content
            );

            System.out.println(
                    "===================================="
            );


            return content;


        } catch (Exception exception) {

            System.err.println();
            System.err.println(
                    "===== AI PROMPT ERROR ====="
            );

            System.err.println(
                    exception.getMessage()
            );

            exception.printStackTrace();

            System.err.println(
                    "==========================="
            );


            throw new RuntimeException(
                    "AI request failed.",
                    exception
            );
        }
    }


    // =========================================================
    // EXECUTE GROQ REQUEST WITH RETRY
    // =========================================================

    private String executeRequestWithRetry(
            Map<String, Object> requestBody) {

        RuntimeException lastException =
                null;


        for (int attempt = 1;
             attempt <= MAX_RETRIES;
             attempt++) {

            try {

                System.out.println();
                System.out.println(
                        "Groq request attempt "
                                + attempt
                                + "/"
                                + MAX_RETRIES
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
                                .bodyValue(
                                        requestBody
                                )
                                .retrieve()
                                .bodyToMono(
                                        Map.class
                                )
                                .block(
                                        Duration.ofSeconds(
                                                60
                                        )
                                );


                String content =
                        extractContent(
                                response
                        );


                if (content == null ||
                        content.isBlank()) {

                    throw new RuntimeException(
                            "Groq returned empty content."
                    );
                }


                return content;


            } catch (
                    WebClientResponseException exception) {


                System.err.println();
                System.err.println(
                        "===== GROQ API ERROR ====="
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
                        "=========================="
                );


                // =============================================
                // 429 RATE LIMIT
                // =============================================

                if (exception.getStatusCode()
                        == HttpStatus.TOO_MANY_REQUESTS) {


                    lastException =
                            new RuntimeException(
                                    "Groq rate limit reached.",
                                    exception
                            );


                    if (attempt < MAX_RETRIES) {


                        long waitMillis =
                                getRetryDelayMillis(
                                        exception,
                                        attempt
                                );


                        System.out.println(
                                "Rate limit reached."
                        );

                        System.out.println(
                                "Waiting "
                                        + waitMillis
                                        + " ms before retry..."
                        );


                        sleep(
                                waitMillis
                        );


                        continue;
                    }


                    break;
                }


                /*
                 * 400 means request/model/output problem.
                 *
                 * Retrying the identical request normally
                 * does not solve it.
                 */

                if (exception.getStatusCode()
                        == HttpStatus.BAD_REQUEST) {

                    throw new RuntimeException(
                            "Groq rejected the request: "
                                    + exception
                                    .getResponseBodyAsString(),
                            exception
                    );
                }


                throw new RuntimeException(
                        "Groq API request failed with status "
                                + exception.getStatusCode(),
                        exception
                );


            } catch (Exception exception) {


                lastException =
                        new RuntimeException(
                                "Groq request failed.",
                                exception
                        );


                if (attempt < MAX_RETRIES) {


                    long waitMillis =
                            1500L * attempt;


                    System.out.println(
                            "Temporary AI failure."
                    );

                    System.out.println(
                            "Retrying in "
                                    + waitMillis
                                    + " ms..."
                    );


                    sleep(
                            waitMillis
                    );


                    continue;
                }
            }
        }


        throw new RuntimeException(
                "Groq request failed after "
                        + MAX_RETRIES
                        + " attempts.",
                lastException
        );
    }


    // =========================================================
    // RETRY DELAY
    // =========================================================

    private long getRetryDelayMillis(
            WebClientResponseException exception,
            int attempt) {


        String retryAfter =
                exception
                        .getHeaders()
                        .getFirst(
                                "Retry-After"
                        );


        if (retryAfter != null) {

            try {

                double seconds =
                        Double.parseDouble(
                                retryAfter.trim()
                        );


                return Math.max(
                        1000L,
                        (long) (
                                seconds * 1000
                        ) + 500L
                );


            } catch (
                    NumberFormatException ignored) {

                // Use fallback below.
            }
        }


        return switch (attempt) {

            case 1 -> 5000L;

            case 2 -> 8000L;

            default -> 10000L;
        };
    }


    // =========================================================
    // SLEEP
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
                    "AI retry interrupted.",
                    exception
            );
        }
    }


    // =========================================================
    // EXTRACT CONTENT FROM GROQ RESPONSE
    // =========================================================

    private String extractContent(
            Map<?, ?> response) {


        if (response == null) {

            throw new RuntimeException(
                    "No response received from Groq."
            );
        }


        Object choicesObject =
                response.get(
                        "choices"
                );


        if (!(choicesObject
                instanceof List<?> choices)
                || choices.isEmpty()) {

            throw new RuntimeException(
                    "No choices returned by Groq."
            );
        }


        Object firstChoiceObject =
                choices.get(
                        0
                );


        if (!(firstChoiceObject
                instanceof Map<?, ?> firstChoice)) {

            throw new RuntimeException(
                    "Invalid choice returned by Groq."
            );
        }


        Object messageObject =
                firstChoice.get(
                        "message"
                );


        if (!(messageObject
                instanceof Map<?, ?> message)) {

            throw new RuntimeException(
                    "No message returned by Groq."
            );
        }


        Object content =
                message.get(
                        "content"
                );


        if (content == null) {

            throw new RuntimeException(
                    "No content returned by Groq."
            );
        }


        return content
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


        // Remove ```json

        if (cleaned.startsWith(
                "```json")) {

            cleaned =
                    cleaned
                            .substring(7)
                            .trim();
        }


        // Remove generic ```

        else if (cleaned.startsWith(
                "```")) {

            cleaned =
                    cleaned
                            .substring(3)
                            .trim();
        }


        // Remove ending ```

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


        /*
         * Extract only JSON object.
         */

        int firstBrace =
                cleaned.indexOf(
                        '{'
                );


        int lastBrace =
                cleaned.lastIndexOf(
                        '}'
                );


        if (firstBrace >= 0 &&
                lastBrace > firstBrace) {


            cleaned =
                    cleaned.substring(
                            firstBrace,
                            lastBrace + 1
                    );
        }


        return cleaned;
    }


    // =========================================================
    // LIMIT TEXT
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
    // FALLBACK CV ANALYSIS
    // =========================================================

    private AIAnalysisResult
    createFallbackResult() {


        AIAnalysisResult result =
                new AIAnalysisResult();


        result.setAtsScore(
                0
        );


        result.setProfileLevel(
                "Analysis unavailable"
        );


        result.setBestJobMatch(
                "Not available"
        );


        result.setStrengths(
                List.of()
        );


        result.setMissingSkills(
                List.of()
        );


        result.setRecommendations(
                List.of(
                        "AI analysis is temporarily unavailable. Please try again."
                )
        );


        return result;
    }
}