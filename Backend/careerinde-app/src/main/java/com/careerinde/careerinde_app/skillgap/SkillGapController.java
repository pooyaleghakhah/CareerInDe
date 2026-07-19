package com.careerinde.careerinde_app.skillgap;

import java.io.File;
import java.io.IOException;

import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;

import com.careerinde.careerinde_app.ai.OpenAIService;

import com.careerinde.careerinde_app.report.AIReport;
import com.careerinde.careerinde_app.report.AIReportRepository;

import com.careerinde.careerinde_app.resume.PdfService;

import com.careerinde.careerinde_app.user.User;
import com.careerinde.careerinde_app.user.UserRepository;

@Controller
public class SkillGapController {

    private final OpenAIService openAIService;

    private final PdfService pdfService;

    private final AIReportRepository aiReportRepository;

    private final UserRepository userRepository;

    public SkillGapController(

            OpenAIService openAIService,

            PdfService pdfService,

            AIReportRepository aiReportRepository,

            UserRepository userRepository) {

        this.openAIService =
                openAIService;

        this.pdfService =
                pdfService;

        this.aiReportRepository =
                aiReportRepository;

        this.userRepository =
                userRepository;
    }

    @GetMapping("/skill-gap")
    public String skillGapPage() {

        return "skill-gap";
    }

    @PostMapping("/skill-gap")
    public String analyzeSkillGap(

            @RequestParam("file")
            MultipartFile file,

            @RequestParam("jobDescription")
            String jobDescription,

            Authentication authentication,

            Model model)
            throws IOException {

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
You are an AI Career Coach
for the German tech market.

Analyze the candidate CV against
the job description.

Return STRICTLY in this format:

CURRENT_SKILLS:
- bullet points

MISSING_SKILLS:
- bullet points

LEARNING_ROADMAP:
1. step
2. step
3. step

CAREER_READINESS:
- Beginner
- Intermediate
- Job Ready

CV:
""" + cvText +

"""

JOB DESCRIPTION:
""" + jobDescription;

        String result =
                openAIService
                        .sendPrompt(prompt);

        String email =
                authentication.getName();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user != null) {

            AIReport report =
                    new AIReport();

            report.setReportType(
                    "SKILL_GAP");

            report.setResult(result);

            report.setUser(user);

            aiReportRepository
                    .save(report);
        }

        model.addAttribute(
                "result",
                result);

        return "skill-gap-result";
    }
}