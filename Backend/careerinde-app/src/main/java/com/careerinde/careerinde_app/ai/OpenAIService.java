package com.careerinde.careerinde_app.ai;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final WebClient webClient =
            WebClient.builder()

                    .baseUrl("https://api.groq.com/openai/v1")

                    .defaultHeader(
                            HttpHeaders.CONTENT_TYPE,
                            MediaType.APPLICATION_JSON_VALUE)

                    .build();

    public String analyzeCV(String cvText) {

        String prompt = """
You are an advanced ATS system and career recruiter
for the German tech market.

Analyze this CV realistically.

The candidate is applying for:
- Junior / Mid-Level Tech Roles
- Digital Transformation
- IT Project Management
- Data & Digital Roles

Return STRICTLY in this format:

ATS_SCORE: number between 0 and 100

STRENGTHS:
- bullet points

MISSING_SKILLS:
- bullet points

RECOMMENDATIONS:
- bullet points

Important:
Do NOT score too harshly.
Consider education, projects,
transferable skills,
international experience,
and technical potential.

CV:
""" + cvText;

        return sendPrompt(prompt);
    }

    public String sendPrompt(String prompt) {

        Map<String, Object> requestBody =
                Map.of(

                        "model",
                        "llama-3.3-70b-versatile",

                        "messages",
                        new Object[] {

                                Map.of(
                                        "role",
                                        "user",

                                        "content",
                                        prompt)
                        });

        try {

            Map response =
                    webClient.post()

                            .uri("/chat/completions")

                            .header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer " + apiKey)

                            .bodyValue(requestBody)

                            .retrieve()

                            .bodyToMono(Map.class)

                            .block();

            return extractContent(response);

        } catch (Exception e) {

            return "OpenAI Error: "
                    + e.getMessage();
        }
    }

    private String extractContent(Map response) {

        try {

            List choices =
                    (List) response.get("choices");

            if (choices == null
                    || choices.isEmpty()) {

                return "No AI response.";
            }

            Map firstChoice =
                    (Map) choices.get(0);

            Map message =
                    (Map) firstChoice.get("message");

            return message.get("content")
                    .toString();

        } catch (Exception e) {

            return "Failed to parse AI response.";
        }
    }
}
