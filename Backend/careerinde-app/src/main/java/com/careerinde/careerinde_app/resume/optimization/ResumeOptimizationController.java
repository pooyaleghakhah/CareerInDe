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


    // =========================================================
    // OPTIMIZE RESUME FOR JOB
    // =========================================================

    @PostMapping("/resume/optimize-for-job")
    public String optimizeResumeForJob(

            @RequestParam(
                    value = "jobDescription",
                    required = false)
            String jobDescription,

            Model model,

            HttpSession session) {


        try {

            // =================================================
            // GET CV FROM SESSION
            // =================================================

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


            cvText =
                    cvText.trim();


            // =================================================
            // JOB DESCRIPTION
            //
            // Priority:
            // 1. POST request
            // 2. latestJobDescription from session
            // =================================================

            jobDescription =
                    normalizeText(
                            jobDescription
                    );


            if (jobDescription == null) {


                Object sessionJobDescription =
                        session.getAttribute(
                                "latestJobDescription"
                        );


                if (sessionJobDescription instanceof String savedJobDescription) {

                    jobDescription =
                            normalizeText(
                                    savedJobDescription
                            );
                }
            }


            // =================================================
            // VALIDATE JOB DESCRIPTION
            // =================================================

            if (jobDescription == null) {


                model.addAttribute(
                        "error",
                        "No target job description was found. Please add a job description before optimizing your CV."
                );


                return "optimized-resume";
            }


            // =================================================
            // TARGET JOB TITLE
            // =================================================

            String jobTitle =
                    null;


            Object sessionJobTitle =
                    session.getAttribute(
                            "latestTargetJobTitle"
                    );


            if (sessionJobTitle instanceof String savedJobTitle) {

                jobTitle =
                        normalizeText(
                                savedJobTitle
                        );
            }


            // =================================================
            // DEBUG
            // =================================================

            System.out.println();

            System.out.println(
                    "======================================"
            );

            System.out.println(
                    "CAREERINDE RESUME OPTIMIZATION REQUEST"
            );

            System.out.println(
                    "CV source: SESSION"
            );

            System.out.println(
                    "CV length: "
                            + cvText.length()
            );

            System.out.println(
                    "Job Description length: "
                            + jobDescription.length()
            );


            if (jobTitle != null) {

                System.out.println(
                        "Target Job: "
                                + jobTitle
                );
            }


            System.out.println(
                    "======================================"
            );


            // =================================================
            // OPTIMIZE
            // =================================================

            OptimizedResume optimizedResume =
                    resumeOptimizationService
                            .optimizeResume(
                                    cvText,
                                    jobDescription
                            );


            // =================================================
            // VALIDATE RESULT
            // =================================================

            if (optimizedResume == null) {


                model.addAttribute(
                        "error",
                        "Resume optimization returned no result. Please try again."
                );


                return "optimized-resume";
            }


            // =================================================
            // SAVE RESULT IN SESSION
            // =================================================

            session.setAttribute(
                    "optimizedResume",
                    optimizedResume
            );


            session.setAttribute(
                    "optimizedResumeJobDescription",
                    jobDescription
            );


            if (jobTitle != null) {

                session.setAttribute(
                        "optimizedResumeJobTitle",
                        jobTitle
                );
            }


            // =================================================
            // SEND RESULT TO VIEW
            // =================================================

            model.addAttribute(
                    "resume",
                    optimizedResume
            );


            model.addAttribute(
                    "jobDescription",
                    jobDescription
            );


            model.addAttribute(
                    "jobTitle",
                    jobTitle
            );


            // =================================================
            // SUCCESS LOG
            // =================================================

            System.out.println();

            System.out.println(
                    "======================================"
            );

            System.out.println(
                    "CAREERINDE RESUME OPTIMIZATION SUCCESS"
            );

            System.out.println(
                    "Original Match Score: "
                            + optimizedResume
                            .getOriginalMatchScore()
                            + "%"
            );

            System.out.println(
                    "Optimized Match Score: "
                            + optimizedResume
                            .getOptimizedMatchScore()
                            + "%"
            );

            System.out.println(
                    "======================================"
            );


            return "optimized-resume";


        } catch (Exception exception) {


            System.err.println();

            System.err.println(
                    "======================================"
            );

            System.err.println(
                    "CAREERINDE RESUME OPTIMIZATION ERROR"
            );


            exception.printStackTrace();


            System.err.println(
                    "======================================"
            );


            model.addAttribute(
                    "error",
                    "We could not optimize your resume right now. Please try again."
            );


            return "optimized-resume";
        }
    }


    // =========================================================
    // TEXT NORMALIZATION
    // =========================================================

    private String normalizeText(
            String value) {


        if (value == null) {

            return null;
        }


        String normalized =
                value.trim();


        if (normalized.isEmpty()) {

            return null;
        }


        return normalized;
    }
}