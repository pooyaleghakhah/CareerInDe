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
                            MediaType.APPLICATION_JSON_VALUE
                    )
                    .build();

    public String analyzeCV(String cvText) {

        String prompt = """
You are an advanced ATS system and career recruiter
specialized in the German tech job market.

Analyze the following CV realistically.

The candidate may be applying for roles such as:
- Junior / Mid-Level Tech Roles
- Software Development
- Data Analysis
- Digital Transformation
- IT Project Management
- Industrial IoT
- Data & Digital Roles

Return STRICTLY in this format:

ATS_SCORE: number between 0 and 100

STRENGTHS:
- bullet points

MISSING_SKILLS:
- bullet points

RECOMMENDATIONS:
- bullet points

Important rules:
- Evaluate the actual content of the CV.
- Do not score too harshly.
- Consider education and academic projects.
- Consider professional experience.
- Consider transferable skills.
- Consider international experience.
- Consider technical skills and potential.
- Consider German job market requirements.
- Do not invent experience or skills that are not present in the CV.
- Give practical recommendations that could improve the candidate's chances in Germany.

CV:
""" + cvText;

        return sendPrompt(prompt);
    }

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
                        }
                );

        try {

            Map response =
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

            return extractContent(response);

        } catch (Exception e) {

            return "AI Error: " + e.getMessage();
        }
    }

    private String extractContent(Map response) {

        try {

            if (response == null) {
                return "No AI response.";
            }

            List choices =
                    (List) response.get("choices");

            if (choices == null || choices.isEmpty()) {
                return "No AI response.";
            }

            Map firstChoice =
                    (Map) choices.get(0);

            Map message =
                    (Map) firstChoice.get("message");

            if (message == null || message.get("content") == null) {
                return "No AI content returned.";
            }

            return message.get("content").toString();

        } catch (Exception e) {

            return "Failed to parse AI response: "
                    + e.getMessage();
        }
    }
}