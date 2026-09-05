package com.careerinde.careerinde_app.ai.gemini;

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
public class GeminiAIService {

    private static final int MAX_ATTEMPTS = 4;

    private static final long[] RETRY_DELAYS = {
            2000L,
            5000L,
            10000L
    };

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private final String apiKey;
    private final String model;


    public GeminiAIService(
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.model:gemini-3.6-flash}") String model) {

        this.apiKey = apiKey;
        this.model = model;

        this.objectMapper = new ObjectMapper();

        this.webClient = WebClient.builder()
                .baseUrl(
                        "https://generativelanguage.googleapis.com/v1"
                )
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }


    // =========================================================
    // GENERAL TEXT GENERATION
    // =========================================================

    public String generateText(String prompt) {

        return generateText(
                prompt,
                0.2,
                2500
        );
    }


    public String generateText(
            String prompt,
            double temperature,
            int maxOutputTokens) {

        validatePrompt(prompt);

        Map<String, Object> generationConfig =
                Map.of(
                        "temperature",
                        temperature,

                        "maxOutputTokens",
                        maxOutputTokens
                );

        return execute(
                prompt,
                generationConfig
        );
    }


    // =========================================================
    // STRUCTURED JSON GENERATION
    // =========================================================

    public String generateJson(
            String prompt,
            Map<String, Object> responseSchema) {

        return generateJson(
                prompt,
                responseSchema,
                0.1,
                5000
        );
    }


    public String generateJson(
            String prompt,
            Map<String, Object> responseSchema,
            double temperature,
            int maxOutputTokens) {

        validatePrompt(prompt);

        if (responseSchema == null ||
                responseSchema.isEmpty()) {

            throw new IllegalArgumentException(
                    "Response schema cannot be empty."
            );
        }

        Map<String, Object> generationConfig =
                Map.of(
                        "temperature",
                        temperature,

                        "maxOutputTokens",
                        maxOutputTokens,

                        "responseMimeType",
                        "application/json",

                        "responseSchema",
                        responseSchema
                );

        String result =
                execute(
                        prompt,
                        generationConfig
                );

        validateJson(
                result
        );

        return result;
    }


    // =========================================================
    // CORE GEMINI REQUEST
    // =========================================================

    private String execute(
            String prompt,
            Map<String, Object> generationConfig) {

        Map<String, Object> requestBody =
                Map.of(
                        "contents",
                        List.of(
                                Map.of(
                                        "role",
                                        "user",

                                        "parts",
                                        List.of(
                                                Map.of(
                                                        "text",
                                                        prompt
                                                )
                                        )
                                )
                        ),

                        "generationConfig",
                        generationConfig
                );


        RuntimeException lastException = null;


        for (int attempt = 1;
             attempt <= MAX_ATTEMPTS;
             attempt++) {

            try {

                logAttempt(
                        attempt
                );


                Map<?, ?> response =
                        webClient
                                .post()

                                .uri(
                                        "/models/"
                                                + model
                                                + ":generateContent"
                                )

                                .header(
                                        "x-goog-api-key",
                                        apiKey
                                )

                                .bodyValue(
                                        requestBody
                                )

                                .retrieve()

                                .bodyToMono(
                                        Map.class
                                )

                                .block(
                                        Duration.ofSeconds(90)
                                );


                String result =
                        extractText(
                                response
                        );


                System.out.println();

                System.out.println(
                        "======================================"
                );

                System.out.println(
                        "GEMINI REQUEST SUCCESS"
                );

                System.out.println(
                        "Attempt: "
                                + attempt
                );

                System.out.println(
                        "======================================"
                );


                return result;


            } catch (WebClientResponseException exception) {

                int status =
                        exception
                                .getStatusCode()
                                .value();


                logApiError(
                        status,
                        exception
                );


                // =============================================
                // IMPORTANT:
                // 429 MUST FAIL FAST
                //
                // Free-tier daily quota exhaustion cannot be
                // fixed by immediately retrying the same call.
                // =============================================

                if (status == 429) {

                    System.err.println();

                    System.err.println(
                            "======================================"
                    );

                    System.err.println(
                            "GEMINI RATE LIMIT / QUOTA EXCEEDED"
                    );

                    System.err.println(
                            "Retry skipped to avoid unnecessary delay."
                    );

                    System.err.println(
                            "======================================"
                    );


                    throw new GeminiQuotaExceededException(
                            "Gemini quota or rate limit exceeded.",
                            exception
                    );
                }


                lastException =
                        new RuntimeException(
                                buildFailureMessage(
                                        status
                                ),
                                exception
                        );


                // =============================================
                // NON-RETRYABLE HTTP ERROR
                // =============================================

                if (!isRetryableStatus(status)) {

                    throw lastException;
                }


                // =============================================
                // MAX ATTEMPTS REACHED
                // =============================================

                if (attempt >= MAX_ATTEMPTS) {

                    break;
                }


                // =============================================
                // RETRY TEMPORARY SERVER ERROR
                // =============================================

                long delay =
                        getRetryDelay(
                                attempt
                        );


                logRetry(
                        attempt,
                        delay,
                        status
                );


                sleep(
                        delay
                );


            } catch (GeminiQuotaExceededException exception) {

                // =============================================
                // DO NOT ALLOW GENERIC CATCH TO RETRY 429
                // =============================================

                throw exception;


            } catch (Exception exception) {

                System.err.println();

                System.err.println(
                        "===== GEMINI INTERNAL ERROR ====="
                );

                System.err.println(
                        "Type: "
                                + exception
                                .getClass()
                                .getSimpleName()
                );

                System.err.println(
                        "Message: "
                                + exception
                                .getMessage()
                );

                System.err.println(
                        "================================="
                );


                lastException =
                        new RuntimeException(
                                "Gemini request failed.",
                                exception
                        );


                if (attempt >= MAX_ATTEMPTS) {

                    break;
                }


                long delay =
                        getRetryDelay(
                                attempt
                        );


                logRetry(
                        attempt,
                        delay,
                        null
                );


                sleep(
                        delay
                );
            }
        }


        throw new RuntimeException(
                "Gemini is temporarily unavailable after "
                        + MAX_ATTEMPTS
                        + " attempts.",
                lastException
        );
    }


    // =========================================================
    // RETRYABLE HTTP STATUS
    // =========================================================

    private boolean isRetryableStatus(
            int status) {

        /*
         * 429 is intentionally NOT retryable here.
         *
         * Temporary Gemini server errors are retried.
         */

        return status == 500
                || status == 502
                || status == 503
                || status == 504;
    }


    // =========================================================
    // RETRY DELAY
    // =========================================================

    private long getRetryDelay(
            int attempt) {

        int index =
                Math.min(
                        attempt - 1,
                        RETRY_DELAYS.length - 1
                );

        return RETRY_DELAYS[index];
    }


    // =========================================================
    // ERROR MESSAGE
    // =========================================================

    private String buildFailureMessage(
            int status) {

        if (status == 429) {

            return "Gemini quota or rate limit exceeded.";
        }


        if (status == 503) {

            return "Gemini service is temporarily unavailable.";
        }


        if (status == 500
                || status == 502
                || status == 504) {

            return "Gemini server is temporarily unavailable.";
        }


        if (status == 401
                || status == 403) {

            return "Gemini authentication failed.";
        }


        return "Gemini API request failed with HTTP status "
                + status
                + ".";
    }


    // =========================================================
    // LOG ATTEMPT
    // =========================================================

    private void logAttempt(
            int attempt) {

        System.out.println();

        System.out.println(
                "======================================"
        );

        System.out.println(
                "CAREERINDE CENTRAL GEMINI"
        );

        System.out.println(
                "Attempt: "
                        + attempt
                        + "/"
                        + MAX_ATTEMPTS
        );

        System.out.println(
                "Model: "
                        + model
        );

        System.out.println(
                "======================================"
        );
    }


    // =========================================================
    // API ERROR LOG
    // =========================================================

    private void logApiError(
            int status,
            WebClientResponseException exception) {

        System.err.println();

        System.err.println(
                "===== GEMINI API ERROR ====="
        );

        System.err.println(
                "HTTP Status: "
                        + status
        );

        System.err.println(
                "Response:"
        );

        System.err.println(
                exception
                        .getResponseBodyAsString()
        );

        System.err.println(
                "============================"
        );
    }


    // =========================================================
    // RETRY LOG
    // =========================================================

    private void logRetry(
            int attempt,
            long delay,
            Integer status) {

        System.out.println();

        System.out.println(
                "Gemini request will retry."
        );

        if (status != null) {

            System.out.println(
                    "HTTP Status: "
                            + status
            );
        }

        System.out.println(
                "Next Attempt: "
                        + (attempt + 1)
        );

        System.out.println(
                "Waiting: "
                        + delay
                        + " ms"
        );
    }


    // =========================================================
    // RESPONSE EXTRACTION
    // =========================================================

    private String extractText(
            Map<?, ?> response) {

        if (response == null) {

            throw new RuntimeException(
                    "Gemini returned no response."
            );
        }


        Object candidatesObject =
                response.get(
                        "candidates"
                );


        if (!(candidatesObject instanceof List<?> candidates)
                || candidates.isEmpty()) {

            throw new RuntimeException(
                    "Gemini returned no candidates."
            );
        }


        Object firstCandidate =
                candidates.get(0);


        if (!(firstCandidate instanceof Map<?, ?> candidate)) {

            throw new RuntimeException(
                    "Invalid Gemini candidate."
            );
        }


        // =====================================================
        // FINISH REASON
        // =====================================================

        Object finishReason =
                candidate.get(
                        "finishReason"
                );


        if (finishReason != null) {

            System.out.println(
                    "Gemini finishReason: "
                            + finishReason
            );


            if ("MAX_TOKENS"
                    .equalsIgnoreCase(
                            finishReason.toString()
                    )) {

                throw new RuntimeException(
                        "Gemini output reached max token limit."
                );
            }
        }


        // =====================================================
        // CONTENT
        // =====================================================

        Object contentObject =
                candidate.get(
                        "content"
                );


        if (!(contentObject instanceof Map<?, ?> content)) {

            throw new RuntimeException(
                    "Gemini returned no content."
            );
        }


        Object partsObject =
                content.get(
                        "parts"
                );


        if (!(partsObject instanceof List<?> parts)) {

            throw new RuntimeException(
                    "Gemini returned no parts."
            );
        }


        StringBuilder result =
                new StringBuilder();


        for (Object partObject : parts) {

            if (!(partObject instanceof Map<?, ?> part)) {

                continue;
            }


            Object text =
                    part.get(
                            "text"
                    );


            if (text != null) {

                result.append(
                        text
                );
            }
        }


        String output =
                cleanOutput(
                        result.toString()
                );


        if (output.isBlank()) {

            throw new RuntimeException(
                    "Gemini returned empty output."
            );
        }


        return output;
    }


    // =========================================================
    // JSON VALIDATION
    // =========================================================

    private void validateJson(
            String json) {

        try {

            JsonNode node =
                    objectMapper.readTree(
                            json
                    );


            if (node == null) {

                throw new RuntimeException(
                        "Gemini JSON is empty."
                );
            }


        } catch (Exception exception) {

            throw new RuntimeException(
                    "Gemini returned invalid JSON.",
                    exception
            );
        }
    }


    // =========================================================
    // CLEAN OUTPUT
    // =========================================================

    private String cleanOutput(
            String output) {

        if (output == null) {

            return "";
        }


        String cleaned =
                output.trim();


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


        return cleaned;
    }


    // =========================================================
    // VALIDATION
    // =========================================================

    private void validatePrompt(
            String prompt) {

        if (prompt == null
                || prompt.isBlank()) {

            throw new IllegalArgumentException(
                    "Gemini prompt cannot be empty."
            );
        }
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


        } catch (InterruptedException exception) {

            Thread.currentThread()
                    .interrupt();


            throw new RuntimeException(
                    "Gemini retry interrupted.",
                    exception
            );
        }
    }


    // =========================================================
    // GET CURRENT MODEL
    // =========================================================

    public String getModel() {

        return model;
    }


    // =========================================================
    // GEMINI QUOTA EXCEPTION
    // =========================================================

    public static class GeminiQuotaExceededException
            extends RuntimeException {

        public GeminiQuotaExceededException(
                String message,
                Throwable cause) {

            super(
                    message,
                    cause
            );
        }
    }
}