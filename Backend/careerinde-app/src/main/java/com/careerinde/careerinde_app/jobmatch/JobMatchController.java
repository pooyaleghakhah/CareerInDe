package com.careerinde.careerinde_app.jobmatch;

import java.io.File;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;

import com.careerinde.careerinde_app.ai.OpenAIService;
import com.careerinde.careerinde_app.resume.PdfService;

@Controller
public class JobMatchController {

    private final OpenAIService openAIService;

    private final PdfService pdfService;

    public JobMatchController(
            OpenAIService openAIService,
            PdfService pdfService) {

        this.openAIService =
                openAIService;

        this.pdfService =
                pdfService;
    }

    @GetMapping("/job-match")
    public String jobMatchPage() {

        return "job-match";
    }

    @PostMapping("/job-match")
    public String analyzeMatch(

            @RequestParam("file")
            MultipartFile file,

            @RequestParam("jobDescription")
            String jobDescription,

            Model model) {

        try {

            String uploadDir =
                    System.getProperty("user.dir")
                            + "/uploads/";

            File directory =
                    new File(uploadDir);

            if (!directory.exists()) {

                directory.mkdirs();
            }

            String fileName =
                    file.getOriginalFilename();

            File destination =
                    new File(uploadDir + fileName);

            file.transferTo(destination);

            String cvText =
                    pdfService.extractText(destination);

            String prompt = """
You are an advanced AI ATS recruiter.

Analyze how well this candidate CV matches
the following job description.

Return STRICTLY in this format:

MATCH_SCORE: number between 0 and 100

STRENGTHS:
- bullet points

MISSING_SKILLS:
- bullet points

HIRING_PROBABILITY:
- Low / Medium / High

RECOMMENDATIONS:
- bullet points

CV:
""" + cvText +

"""

JOB DESCRIPTION:
""" + jobDescription;

            String result =
                    openAIService
                            .sendPrompt(prompt);

            model.addAttribute(
                    "result",
                    result);

            return "job-match-result";

        } catch (Exception e) {

            model.addAttribute(
                    "result",
                    "ERROR: " + e.getMessage());

            return "job-match-result";
        }
    }
}
