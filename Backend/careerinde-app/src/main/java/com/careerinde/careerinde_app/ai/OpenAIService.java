package com.careerinde.careerinde_app.ai;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.careerinde.careerinde_app.AIAnalysisResult.AIAnalysisResult;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;


    public OpenAIService(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;

        this.webClient = WebClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }


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

Return ONLY valid JSON.

Do NOT use Markdown.
Do NOT use ```json.
Do NOT include any text before or after the JSON.

Return exactly this JSON structure:

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
    "Add measurable achievements to work experience",
    "Strengthen Docker experience",
    "Highlight backend projects more prominently"
  ]
}

Rules:

atsScore:
- Must be an integer between 0 and 100.
- Evaluate the actual CV content.
- Do not score too harshly.
- Consider education and academic projects.
- Consider professional experience.
- Consider transferable skills.
- Consider international experience.
- Consider German job market requirements.
- Do not invent skills or experience.

profileLevel must be exactly one of:
- Needs Improvement
- Developing
- Good
- Strong
- Excellent

bestJobMatch:
- Choose ONE realistic job role.
- Base it only on the candidate's actual profile.

strengths:
- Return 3 to 6 concise strengths.
- Base them on evidence in the CV.

missingSkills:
- Return 0 to 6 relevant missing skills.
- Only suggest skills relevant to the candidate's likely career path.
- Do not claim a skill is missing if it already appears in the CV.

recommendations:
- Return 3 to 6 specific and actionable recommendations.
- Focus on improving the candidate's chances in the German job market.

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
                        0.2
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


            String content = extractContent(response);

            String cleanedJson = cleanJson(content);


            AIAnalysisResult result =
                    objectMapper.readValue(
                            cleanedJson,
                            AIAnalysisResult.class
                    );


            // ATS Score safety validation
            if (result.getAtsScore() < 0) {
                result.setAtsScore(0);
            }

            if (result.getAtsScore() > 100) {
                result.setAtsScore(100);
            }


            // Prevent null lists
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


        } catch (Exception e) {

            System.err.println(
                    "AI Analysis Error: " + e.getMessage()
            );

            e.printStackTrace();

            return createFallbackResult();
        }
    }


    private String extractContent(Map response) {

        if (response == null) {
            throw new RuntimeException(
                    "No response received from Groq."
            );
        }


        List choices =
                (List) response.get("choices");


        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException(
                    "No choices returned by Groq."
            );
        }


        Map firstChoice =
                (Map) choices.get(0);


        Map message =
                (Map) firstChoice.get("message");


        if (message == null) {
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


    private String cleanJson(String response) {

        if (response == null) {
            return "";
        }


        String cleaned = response.trim();


        if (cleaned.startsWith("```json")) {

            cleaned =
                    cleaned.substring(7).trim();

        } else if (cleaned.startsWith("```")) {

            cleaned =
                    cleaned.substring(3).trim();
        }


        if (cleaned.endsWith("```")) {

            cleaned =
                    cleaned.substring(
                            0,
                            cleaned.length() - 3
                    ).trim();
        }


        // Extra protection if the model adds text
        // before or after the JSON object.

        int firstBrace =
                cleaned.indexOf('{');

        int lastBrace =
                cleaned.lastIndexOf('}');


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


	public String sendPrompt(String prompt) {
		// TODO Auto-generated method stub
		return null;
	}
}