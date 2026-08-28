package com.careerinde.careerinde_app.resume.optimization;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ResumeOptimizationController {

    private final ResumeOptimizationService resumeOptimizationService;

    public ResumeOptimizationController(
            ResumeOptimizationService resumeOptimizationService) {

        this.resumeOptimizationService =
                resumeOptimizationService;
    }

    @PostMapping("/resume/optimize-for-job")
    public String optimizeResumeForJob(

            @RequestParam("jobDescription")
            String jobDescription,

            Model model,

            HttpSession session) {

        try {

            Object sessionCvText =
                    session.getAttribute(
                            "latestCvText"
                    );

            if (!(sessionCvText instanceof String cvText)
                    || cvText.isBlank()) {

                model.addAttribute(
                        "error",
                        "No CV found. Please upload and analyze your CV first."
                );

                return "optimized-resume";
            }

            if (jobDescription == null ||
                    jobDescription.isBlank()) {

                model.addAttribute(
                        "error",
                        "Job description is missing."
                );

                return "optimized-resume";
            }

            OptimizedResume optimizedResume =
                    resumeOptimizationService
                            .optimizeResume(
                                    cvText,
                                    jobDescription
                            );

            session.setAttribute(
                    "optimizedResume",
                    optimizedResume
            );

            session.setAttribute(
                    "optimizedResumeJobDescription",
                    jobDescription
            );

            model.addAttribute(
                    "resume",
                    optimizedResume
            );

            model.addAttribute(
                    "jobDescription",
                    jobDescription
            );

            return "optimized-resume";

        } catch (Exception exception) {

            System.err.println(
                    "===== OPTIMIZED RESUME CONTROLLER ERROR ====="
            );

            exception.printStackTrace();

            System.err.println(
                    "============================================="
            );

            model.addAttribute(
                    "error",
                    "We could not optimize your resume right now. Please try again."
            );

            return "optimized-resume";
        }
    }
}