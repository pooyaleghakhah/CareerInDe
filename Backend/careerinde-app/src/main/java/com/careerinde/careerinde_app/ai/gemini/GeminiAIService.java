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
                .baseUrl("https://generativelanguage.googleapis.com/v1")
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
                        "temperature", temperature,
                        "maxOutputTokens", maxOutputTokens
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

        String result = execute(
                prompt,
                generationConfig
        );

        validateJson(result);

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


        int maxAttempts = 2;

        RuntimeException lastException = null;


        for (int attempt = 1;
             attempt <= maxAttempts;
             attempt++) {

            try {

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
                                + maxAttempts
                );

                System.out.println(
                        "Model: "
                                + model
                );

                System.out.println(
                        "======================================"
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


                return extractText(
                        response
                );


            } catch (WebClientResponseException exception) {

                System.err.println();
                System.err.println(
                        "===== GEMINI API ERROR ====="
                );

                System.err.println(
                        "Status: "
                                + exception.getStatusCode()
                );

                System.err.println(
                        "Response: "
                                + exception.getResponseBodyAsString()
                );

                System.err.println(
                        "============================"
                );


                lastException =
                        new RuntimeException(
                                "Gemini API request failed.",
                                exception
                        );


                int status =
                        exception
                                .getStatusCode()
                                .value();


                /*
                 * Retry:
                 * 429 Rate Limit
                 * 5xx Server Errors
                 */
                boolean retryable =
                        status == 429 ||
                        status >= 500;


                if (!retryable) {

                    throw lastException;
                }


            } catch (Exception exception) {

                System.err.println();
                System.err.println(
                        "===== GEMINI ERROR ====="
                );

                System.err.println(
                        exception.getMessage()
                );

                System.err.println(
                        "========================"
                );


                lastException =
                        new RuntimeException(
                                "Gemini request failed.",
                                exception
                        );
            }


            if (attempt < maxAttempts) {

                sleep(
                        3000L * attempt
                );
            }
        }


        throw new RuntimeException(
                "Gemini request failed after retries.",
                lastException
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


        if (!(candidatesObject
                instanceof List<?> candidates)
                ||
                candidates.isEmpty()) {

            throw new RuntimeException(
                    "Gemini returned no candidates."
            );
        }


        Object firstCandidate =
                candidates.get(0);


        if (!(firstCandidate
                instanceof Map<?, ?> candidate)) {

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


        if (!(contentObject
                instanceof Map<?, ?> content)) {

            throw new RuntimeException(
                    "Gemini returned no content."
            );
        }


        Object partsObject =
                content.get(
                        "parts"
                );


        if (!(partsObject
                instanceof List<?> parts)) {

            throw new RuntimeException(
                    "Gemini returned no parts."
            );
        }


        StringBuilder result =
                new StringBuilder();


        for (Object partObject : parts) {

            if (!(partObject
                    instanceof Map<?, ?> part)) {

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

        if (prompt == null ||
                prompt.isBlank()) {

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
}