package com.careerinde.careerinde_app.jobmatch;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.careerinde.careerinde_app.ai.OpenAIService;
import com.careerinde.careerinde_app.resume.PdfService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class JobMatchController {

    private final OpenAIService openAIService;
    private final PdfService pdfService;

    // We create our own ObjectMapper here
    // to avoid the Spring Bean problem we had before.
    private final ObjectMapper objectMapper = new ObjectMapper();


    public JobMatchController(
            OpenAIService openAIService,
            PdfService pdfService) {

        this.openAIService = openAIService;
        this.pdfService = pdfService;
    }


    // =========================================
    // Show Job Match page
    // =========================================

    @GetMapping("/job-match")
    public String jobMatchPage() {

        return "job-match";
    }


    // =========================================
    // Analyze CV against Job Description
    // =========================================

    @PostMapping("/job-match")
    public String analyzeMatch(

            @RequestParam("file")
            MultipartFile file,

            @RequestParam("jobDescription")
            String jobDescription,

            Model model) {

        try {

            // =========================================
            // Validate uploaded CV
            // =========================================

            if (file == null || file.isEmpty()) {

                model.addAttribute(
                        "error",
                        "Please upload your CV."
                );

                return "job-match-result";
            }


            // =========================================
            // Validate Job Description
            // =========================================

            if (jobDescription == null
                    || jobDescription.isBlank()) {

                model.addAttribute(
                        "error",
                        "Please enter a job description."
                );

                return "job-match-result";
            }


            // =========================================
            // Create upload directory
            // =========================================

            String uploadDir =
                    System.getProperty("user.dir")
                            + "/uploads/";

            File directory =
                    new File(uploadDir);

            if (!directory.exists()) {
                directory.mkdirs();
            }


            // =========================================
            // Save uploaded CV
            // =========================================

            String fileName =
                    file.getOriginalFilename();

            if (fileName == null
                    || fileName.isBlank()) {

                fileName = "uploaded-cv.pdf";
            }


            File destination =
                    new File(
                            directory,
                            fileName
                    );

            file.transferTo(destination);


            // =========================================
            // Extract CV text
            // =========================================

            String cvText =
                    pdfService.extractText(
                            destination
                    );


            if (cvText == null
                    || cvText.isBlank()) {

                model.addAttribute(
                        "error",
                        "Could not extract text from the CV."
                );

                return "job-match-result";
            }


            // =========================================
            // Create AI Prompt
            // =========================================

            String prompt = """
You are an advanced ATS recruiter specialized
in the German job market.

Analyze how well the candidate's CV matches
the provided job description.

Return ONLY valid JSON.

Do NOT use Markdown.
Do NOT use ```json.
Do NOT include explanations before or after JSON.

Return exactly this JSON structure:

{
  "matchScore": 82,
  "matchLevel": "Strong Match",
  "skillsMatch": 85,
  "experienceMatch": 80,
  "educationMatch": 90,
  "matchingSkills": [
    "Java",
    "Spring Boot",
    "PostgreSQL"
  ],
  "missingSkills": [
    "Docker",
    "Kubernetes"
  ],
  "hiringProbability": "High",
  "recommendations": [
    "Highlight Spring Boot projects",
    "Add measurable achievements",
    "Gain practical Docker experience"
  ]
}

Rules:

matchScore:
- Integer between 0 and 100.
- Represents overall CV-to-job compatibility.

matchLevel:
- Must be exactly one of:
  "Weak Match"
  "Moderate Match"
  "Good Match"
  "Strong Match"
  "Excellent Match"

skillsMatch:
- Integer between 0 and 100.
- Compare required technical and professional skills.

experienceMatch:
- Integer between 0 and 100.
- Compare work experience with job requirements.

educationMatch:
- Integer between 0 and 100.
- Compare education with job requirements.

matchingSkills:
- Return skills that exist in BOTH the CV
  and the job description.
- Do not invent skills.

missingSkills:
- Return important skills required by the job
  that are not clearly present in the CV.
- Do not list a skill as missing if it exists
  in the CV.

hiringProbability:
- Must be exactly:
  "Low"
  "Medium"
  "High"

recommendations:
- Return 3 to 6 specific recommendations.
- Recommendations must help the candidate
  improve their application for THIS job.
- Do not invent experience.
- Do not invent education.
- Do not invent certifications.

CANDIDATE CV:

""" + cvText + """

JOB DESCRIPTION:

""" + jobDescription;


            // =========================================
            // Send prompt to AI
            // =========================================

            String aiResponse =
                    openAIService.sendPrompt(
                            prompt
                    );


            if (aiResponse == null
                    || aiResponse.isBlank()) {

                throw new RuntimeException(
                        "AI returned an empty response."
                );
            }


            // =========================================
            // Clean AI JSON response
            // =========================================

            String cleanedJson =
                    cleanJson(aiResponse);


            // =========================================
            // Convert JSON to JobMatchResult
            // =========================================

            JobMatchResult result =
                    objectMapper.readValue(
                            cleanedJson,
                            JobMatchResult.class
                    );


            // =========================================
            // Safety validation
            // =========================================

            result.setMatchScore(
                    clampScore(
                            result.getMatchScore()
                    )
            );

            result.setSkillsMatch(
                    clampScore(
                            result.getSkillsMatch()
                    )
            );

            result.setExperienceMatch(
                    clampScore(
                            result.getExperienceMatch()
                    )
            );

            result.setEducationMatch(
                    clampScore(
                            result.getEducationMatch()
                    )
            );


            // =========================================
            // Prevent null values
            // =========================================

            if (result.getMatchingSkills() == null) {
                result.setMatchingSkills(
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


            // =========================================
            // Send structured data to Thymeleaf
            // =========================================

            model.addAttribute(
                    "matchResult",
                    result
            );

            model.addAttribute(
                    "matchScore",
                    result.getMatchScore()
            );

            model.addAttribute(
                    "matchLevel",
                    result.getMatchLevel()
            );

            model.addAttribute(
                    "skillsMatch",
                    result.getSkillsMatch()
            );

            model.addAttribute(
                    "experienceMatch",
                    result.getExperienceMatch()
            );

            model.addAttribute(
                    "educationMatch",
                    result.getEducationMatch()
            );

            model.addAttribute(
                    "matchingSkills",
                    result.getMatchingSkills()
            );

            model.addAttribute(
                    "missingSkills",
                    result.getMissingSkills()
            );

            model.addAttribute(
                    "hiringProbability",
                    result.getHiringProbability()
            );

            model.addAttribute(
                    "recommendations",
                    result.getRecommendations()
            );


            return "job-match-result";


        } catch (Exception e) {

            System.err.println(
                    "Job Match Error: "
                            + e.getMessage()
            );

            e.printStackTrace();

            model.addAttribute(
                    "error",
                    "Job match analysis is temporarily unavailable. Please try again."
            );

            return "job-match-result";
        }
    }


    // =========================================
    // Keep scores between 0 and 100
    // =========================================

    private int clampScore(int score) {

        if (score < 0) {
            return 0;
        }

        if (score > 100) {
            return 100;
        }

        return score;
    }


    // =========================================
    // Clean JSON returned by AI
    // =========================================

    private String cleanJson(String response) {

        if (response == null) {
            return "";
        }

        String cleaned =
                response.trim();


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
}