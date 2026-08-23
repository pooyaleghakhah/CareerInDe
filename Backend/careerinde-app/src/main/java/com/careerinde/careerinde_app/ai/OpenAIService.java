package com.careerinde.careerinde_app.ai;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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

    // =========================================================
    // Constructor
    // =========================================================

    public OpenAIService() {

        this.objectMapper = new ObjectMapper();

        this.webClient = WebClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    // =========================================================
    // Structured CV Analysis
    // =========================================================

    public AIAnalysisResult analyzeCV(String cvText) {

        String prompt = """
You are an advanced ATS system and career advisor
specialized in the German tech job market.

Analyze the following CV realistically.

Evaluate:
- technical skills
- professional experience
- education
- academic and personal projects
- transferable skills
- international experience
- language skills
- German job market readiness

Return ONLY one valid JSON object.

Do NOT use Markdown.
Do NOT use code fences.
Do NOT include explanations before or after the JSON.
Keep every strength, missing skill and recommendation concise.

Return exactly these fields:

{
  "atsScore": 78,
  "profileLevel": "Strong",
  "bestJobMatch": "Java Backend Developer",
  "strengths": [
    "Strong Java and Spring Boot foundation",
    "Relevant data analysis experience",
    "Experience with PostgreSQL"
  ],
  "missingSkills": [
    "Docker",
    "Kubernetes"
  ],
  "recommendations": [
    "Add measurable achievements",
    "Strengthen Docker experience",
    "Highlight backend projects"
  ]
}

Rules:

atsScore:
- Integer between 0 and 100.
- Evaluate the actual CV.
- Do not score too harshly.

profileLevel must be exactly one of:
- Needs Improvement
- Developing
- Good
- Strong
- Excellent

bestJobMatch:
- Choose ONE realistic job role.
- Use only evidence from the CV.

strengths:
- Return 3 to 5 concise strengths.
- Use evidence from the CV.

missingSkills:
- Return 0 to 5 relevant missing skills.
- Do not list skills already present in the CV.

recommendations:
- Return 3 to 5 concise and actionable recommendations.
- Focus on the German job market.

Never invent:
- experience
- education
- skills
- certifications
- employers
- achievements

CV:

""" + cvText;

        Map<String, Object> requestBody =
                Map.of(
                        "model",
                        "openai/gpt-oss-20b",

                        "messages",
                        new Object[]{
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
                        2000,

                        "response_format",
                        Map.of(
                                "type",
                                "json_object"
                        )
                );

        try {

            Map<?, ?> response =
                    webClient.post()
                            .uri("/chat/completions")
                            .header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer " + apiKey
                            )
                            .bodyValue(requestBody)
                            .retrieve()
                            .bodyToMono(Map.class)
                            .block();

            String content =
                    extractContent(response);

            System.out.println(
                    "===== GROQ RAW CV ANALYSIS ====="
            );

            System.out.println(content);

            System.out.println(
                    "================================"
            );

            String cleanedJson =
                    cleanJson(content);

            AIAnalysisResult result =
                    objectMapper.readValue(
                            cleanedJson,
                            AIAnalysisResult.class
                    );

            // =================================================
            // ATS Score validation
            // =================================================

            if (result.getAtsScore() < 0) {
                result.setAtsScore(0);
            }

            if (result.getAtsScore() > 100) {
                result.setAtsScore(100);
            }

            // =================================================
            // Prevent null lists
            // =================================================

            if (result.getStrengths() == null) {
                result.setStrengths(List.of());
            }

            if (result.getMissingSkills() == null) {
                result.setMissingSkills(List.of());
            }

            if (result.getRecommendations() == null) {
                result.setRecommendations(List.of());
            }

            return result;

        } catch (WebClientResponseException e) {

            System.err.println();
            System.err.println("===== GROQ API ERROR =====");
            System.err.println(
                    "Status: " + e.getStatusCode()
            );
            System.err.println(
                    "Response: " + e.getResponseBodyAsString()
            );
            System.err.println(
                    "=========================="
            );
            System.err.println();

            return createFallbackResult();

        } catch (Exception e) {

            System.err.println();
            System.err.println(
                    "===== AI ANALYSIS ERROR ====="
            );

            System.err.println(
                    "Message: " + e.getMessage()
            );

            e.printStackTrace();

            System.err.println(
                    "============================="
            );
            System.err.println();

            return createFallbackResult();
        }
    }

    // =========================================================
    // Generic AI Prompt
    // Used by other AI features
    // =========================================================

    public String sendPrompt(String prompt) {

        Map<String, Object> requestBody =
                Map.of(
                        "model",
                        "openai/gpt-oss-20b",

                        "messages",
                        new Object[]{
                                Map.of(
                                        "role",
                                        "user",
                                        "content",
                                        prompt
                                )
                        },

                        "temperature",
                        0.2,

                        "max_tokens",
                        2500
                );

        try {

            Map<?, ?> response =
                    webClient.post()
                            .uri("/chat/completions")
                            .header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer " + apiKey
                            )
                            .bodyValue(requestBody)
                            .retrieve()
                            .bodyToMono(Map.class)
                            .block();

            String content =
                    extractContent(response);

            System.out.println(
                    "===== GROQ RAW PROMPT RESPONSE ====="
            );

            System.out.println(content);

            System.out.println(
                    "===================================="
            );

            return content;

        } catch (WebClientResponseException e) {

            System.err.println();
            System.err.println(
                    "===== GROQ PROMPT API ERROR ====="
            );

            System.err.println(
                    "Status: " + e.getStatusCode()
            );

            System.err.println(
                    "Response: " + e.getResponseBodyAsString()
            );

            System.err.println(
                    "================================="
            );
            System.err.println();

            return "AI service temporarily unavailable.";

        } catch (Exception e) {

            System.err.println();
            System.err.println(
                    "===== AI PROMPT ERROR ====="
            );

            System.err.println(
                    "Message: " + e.getMessage()
            );

            e.printStackTrace();

            System.err.println(
                    "==========================="
            );
            System.err.println();

            return "AI service temporarily unavailable.";
        }
    }

    // =========================================================
    // Extract content from Groq response
    // =========================================================

    private String extractContent(
            Map<?, ?> response) {

        if (response == null) {

            throw new RuntimeException(
                    "No response received from Groq."
            );
        }

        Object choicesObject =
                response.get("choices");

        if (!(choicesObject instanceof List<?> choices)
                || choices.isEmpty()) {

            throw new RuntimeException(
                    "No choices returned by Groq."
            );
        }

        Object firstChoiceObject =
                choices.get(0);

        if (!(firstChoiceObject
                instanceof Map<?, ?> firstChoice)) {

            throw new RuntimeException(
                    "Invalid choice returned by Groq."
            );
        }

        Object messageObject =
                firstChoice.get("message");

        if (!(messageObject
                instanceof Map<?, ?> message)) {

            throw new RuntimeException(
                    "No message returned by Groq."
            );
        }

        Object content =
                message.get("content");

        if (content == null) {

            throw new RuntimeException(
                    "No content returned by Groq."
            );
        }

        return content.toString();
    }

    // =========================================================
    // Clean JSON returned by AI
    // =========================================================

    private String cleanJson(
            String response) {

        if (response == null) {
            return "";
        }

        String cleaned =
                response.trim();

        if (cleaned.startsWith("```json")) {

            cleaned =
                    cleaned.substring(7)
                            .trim();

        } else if (cleaned.startsWith("```")) {

            cleaned =
                    cleaned.substring(3)
                            .trim();
        }

        if (cleaned.endsWith("```")) {

            cleaned =
                    cleaned.substring(
                            0,
                            cleaned.length() - 3
                    ).trim();
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
    // Fallback
    // =========================================================

    private AIAnalysisResult createFallbackResult() {

        AIAnalysisResult result =
                new AIAnalysisResult();

        result.setAtsScore(0);

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