package com.careerinde.careerinde_app.resume.optimization;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.careerinde.careerinde_app.job.matching.scoring.JobMatchScore;
import com.careerinde.careerinde_app.job.matching.scoring.JobMatchScoringService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ResumeOptimizationService {

    private final GeminiResumeAIService geminiResumeAIService;

    private final JobMatchScoringService jobMatchScoringService;

    private final ObjectMapper objectMapper;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ResumeOptimizationService(
            GeminiResumeAIService geminiResumeAIService,
            JobMatchScoringService jobMatchScoringService) {

        this.geminiResumeAIService =
                geminiResumeAIService;

        this.jobMatchScoringService =
                jobMatchScoringService;

        this.objectMapper =
                new ObjectMapper();

        this.objectMapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false
        );
    }


    // =========================================================
    // OPTIMIZE RESUME
    // =========================================================

    public OptimizedResume optimizeResume(
            String originalCvText,
            String jobDescription) {

        validateInput(
                originalCvText,
                jobDescription
        );

        try {

            System.out.println();
            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "CAREERINDE RESUME OPTIMIZATION"
            );

            System.out.println(
                    "Provider: Gemini"
            );

            System.out.println(
                    "Match Engine: CareerInDe"
            );

            System.out.println(
                    "Original CV length: "
                            + originalCvText.length()
            );

            System.out.println(
                    "Job description length: "
                            + jobDescription.length()
            );

            System.out.println(
                    "=========================================="
            );


            // =================================================
            // STEP 1
            // CALCULATE ORIGINAL MATCH SCORE
            // =================================================

            JobMatchScore originalMatch =
                    jobMatchScoringService
                            .calculateScore(
                                    originalCvText,
                                    jobDescription
                            );


            int originalScore =
                    originalMatch.getOverallScore();


            System.out.println();
            System.out.println(
                    "Original CareerInDe Match Score: "
                            + originalScore
                            + "%"
            );


            // =================================================
            // STEP 2
            // ASK GEMINI TO OPTIMIZE RESUME
            // =================================================

            String aiResponse =
                    geminiResumeAIService
                            .optimizeResume(
                                    originalCvText,
                                    jobDescription
                            );


            if (aiResponse == null ||
                    aiResponse.isBlank()) {

                throw new RuntimeException(
                        "Gemini returned an empty response."
                );
            }


            System.out.println();
            System.out.println(
                    "===== GEMINI OPTIMIZED RESUME JSON ====="
            );

            System.out.println(
                    aiResponse
            );

            System.out.println(
                    "=========================================="
            );


            // =================================================
            // STEP 3
            // CONVERT JSON TO JAVA OBJECT
            // =================================================

            OptimizedResume optimizedResume =
                    objectMapper.readValue(
                            aiResponse,
                            OptimizedResume.class
                    );


            normalizeResult(
                    optimizedResume
            );


            // =================================================
            // STEP 4
            // CONVERT OPTIMIZED RESUME TO TEXT
            // =================================================

            String optimizedResumeText =
                    buildResumeText(
                            optimizedResume
                    );


            // =================================================
            // STEP 5
            // RECALCULATE SCORE USING CAREERINDE ENGINE
            // =================================================

            JobMatchScore optimizedMatch =
                    jobMatchScoringService
                            .calculateScore(
                                    optimizedResumeText,
                                    jobDescription
                            );


            int optimizedScore =
                    optimizedMatch.getOverallScore();


            // =================================================
            // STEP 6
            // CAREERINDE OWNS THE SCORES
            // =================================================

            optimizedResume.setOriginalMatchScore(
                    originalScore
            );


            optimizedResume.setOptimizedMatchScore(
                    optimizedScore
            );


            // =================================================
            // DEBUG
            // =================================================

            System.out.println();
            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "CAREERINDE FINAL MATCH RESULT"
            );

            System.out.println(
                    "Original Score: "
                            + originalScore
                            + "%"
            );

            System.out.println(
                    "Optimized Score: "
                            + optimizedScore
                            + "%"
            );

            System.out.println(
                    "Improvement: "
                            + (optimizedScore - originalScore)
                            + "%"
            );

            System.out.println(
                    "Score Provider: CareerInDe Match Engine"
            );

            System.out.println(
                    "=========================================="
            );


            return optimizedResume;


        } catch (Exception exception) {

            System.err.println();
            System.err.println(
                    "=========================================="
            );

            System.err.println(
                    "RESUME OPTIMIZATION ERROR"
            );

            System.err.println(
                    exception.getMessage()
            );

            exception.printStackTrace();

            System.err.println(
                    "=========================================="
            );


            throw new RuntimeException(
                    "Resume optimization is temporarily unavailable.",
                    exception
            );
        }
    }


    // =========================================================
    // BUILD OPTIMIZED RESUME TEXT
    // =========================================================

    private String buildResumeText(
            OptimizedResume resume) {

        StringBuilder builder =
                new StringBuilder();


        appendLine(
                builder,
                resume.getFullName()
        );

        appendLine(
                builder,
                resume.getTargetRole()
        );

        appendLine(
                builder,
                resume.getEmail()
        );

        appendLine(
                builder,
                resume.getPhone()
        );

        appendLine(
                builder,
                resume.getLocation()
        );

        appendLine(
                builder,
                resume.getLinkedin()
        );

        appendLine(
                builder,
                resume.getGithub()
        );


        // -----------------------------------------------------
        // PROFESSIONAL SUMMARY
        // -----------------------------------------------------

        appendLine(
                builder,
                resume.getProfessionalSummary()
        );


        // -----------------------------------------------------
        // SKILLS
        // -----------------------------------------------------

        if (resume.getSkills() != null) {

            resume.getSkills()
                    .forEach(
                            skill ->
                                    appendLine(
                                            builder,
                                            skill
                                    )
                    );
        }


        // -----------------------------------------------------
        // EXPERIENCE
        // -----------------------------------------------------

        if (resume.getExperiences() != null) {

            resume.getExperiences()
                    .forEach(
                            experience -> {

                                appendLine(
                                        builder,
                                        experience.getJobTitle()
                                );

                                appendLine(
                                        builder,
                                        experience.getCompany()
                                );

                                appendLine(
                                        builder,
                                        experience.getLocation()
                                );

                                appendLine(
                                        builder,
                                        experience.getStartDate()
                                );

                                appendLine(
                                        builder,
                                        experience.getEndDate()
                                );


                                if (experience.getBulletPoints()
                                        != null) {

                                    experience.getBulletPoints()
                                            .forEach(
                                                    bullet ->
                                                            appendLine(
                                                                    builder,
                                                                    bullet
                                                            )
                                            );
                                }
                            }
                    );
        }


        // -----------------------------------------------------
        // EDUCATION
        // -----------------------------------------------------

        if (resume.getEducation() != null) {

            resume.getEducation()
                    .forEach(
                            education -> {

                                appendLine(
                                        builder,
                                        education.getDegree()
                                );

                                appendLine(
                                        builder,
                                        education.getFieldOfStudy()
                                );

                                appendLine(
                                        builder,
                                        education.getInstitution()
                                );

                                appendLine(
                                        builder,
                                        education.getLocation()
                                );

                                appendLine(
                                        builder,
                                        education.getStartDate()
                                );

                                appendLine(
                                        builder,
                                        education.getEndDate()
                                );

                                appendLine(
                                        builder,
                                        education.getGrade()
                                );
                            }
                    );
        }


        // -----------------------------------------------------
        // PROJECTS
        // -----------------------------------------------------

        if (resume.getProjects() != null) {

            resume.getProjects()
                    .forEach(
                            project -> {

                                appendLine(
                                        builder,
                                        project.getTitle()
                                );

                                appendLine(
                                        builder,
                                        project.getDescription()
                                );


                                if (project.getTechnologies()
                                        != null) {

                                    project.getTechnologies()
                                            .forEach(
                                                    technology ->
                                                            appendLine(
                                                                    builder,
                                                                    technology
                                                            )
                                            );
                                }


                                if (project.getBulletPoints()
                                        != null) {

                                    project.getBulletPoints()
                                            .forEach(
                                                    bullet ->
                                                            appendLine(
                                                                    builder,
                                                                    bullet
                                                            )
                                            );
                                }
                            }
                    );
        }


        // -----------------------------------------------------
        // LANGUAGES
        // -----------------------------------------------------

        if (resume.getLanguages() != null) {

            resume.getLanguages()
                    .forEach(
                            language ->
                                    appendLine(
                                            builder,
                                            language
                                    )
                    );
        }


        // -----------------------------------------------------
        // CERTIFICATIONS
        // -----------------------------------------------------

        if (resume.getCertifications() != null) {

            resume.getCertifications()
                    .forEach(
                            certification ->
                                    appendLine(
                                            builder,
                                            certification
                                    )
                    );
        }


        return builder
                .toString()
                .trim();
    }


    // =========================================================
    // APPEND SAFE LINE
    // =========================================================

    private void appendLine(
            StringBuilder builder,
            String value) {

        if (value == null ||
                value.isBlank()) {

            return;
        }


        builder
                .append(value.trim())
                .append(System.lineSeparator());
    }


    // =========================================================
    // NORMALIZE RESULT
    // =========================================================

    private void normalizeResult(
            OptimizedResume resume) {

        if (resume == null) {

            throw new RuntimeException(
                    "Gemini returned no resume object."
            );
        }


        if (resume.getFullName() == null) {
            resume.setFullName("");
        }


        if (resume.getTargetRole() == null) {
            resume.setTargetRole("");
        }


        if (resume.getEmail() == null) {
            resume.setEmail("");
        }


        if (resume.getPhone() == null) {
            resume.setPhone("");
        }


        if (resume.getLocation() == null) {
            resume.setLocation("");
        }


        if (resume.getLinkedin() == null) {
            resume.setLinkedin("");
        }


        if (resume.getGithub() == null) {
            resume.setGithub("");
        }


        if (resume.getProfessionalSummary() == null) {
            resume.setProfessionalSummary("");
        }


        if (resume.getSkills() == null) {

            resume.setSkills(
                    new ArrayList<>()
            );
        }


        if (resume.getExperiences() == null) {

            resume.setExperiences(
                    new ArrayList<>()
            );
        }


        if (resume.getEducation() == null) {

            resume.setEducation(
                    new ArrayList<>()
            );
        }


        if (resume.getProjects() == null) {

            resume.setProjects(
                    new ArrayList<>()
            );
        }


        if (resume.getLanguages() == null) {

            resume.setLanguages(
                    new ArrayList<>()
            );
        }


        if (resume.getCertifications() == null) {

            resume.setCertifications(
                    new ArrayList<>()
            );
        }


        // -----------------------------------------------------
        // NORMALIZE EXPERIENCES
        // -----------------------------------------------------

        resume.getExperiences()
                .forEach(
                        experience -> {

                            if (experience.getJobTitle() == null) {
                                experience.setJobTitle("");
                            }

                            if (experience.getCompany() == null) {
                                experience.setCompany("");
                            }

                            if (experience.getLocation() == null) {
                                experience.setLocation("");
                            }

                            if (experience.getStartDate() == null) {
                                experience.setStartDate("");
                            }

                            if (experience.getEndDate() == null) {
                                experience.setEndDate("");
                            }

                            if (experience.getBulletPoints() == null) {

                                experience.setBulletPoints(
                                        new ArrayList<>()
                                );
                            }
                        }
                );


        // -----------------------------------------------------
        // NORMALIZE EDUCATION
        // -----------------------------------------------------

        resume.getEducation()
                .forEach(
                        education -> {

                            if (education.getDegree() == null) {
                                education.setDegree("");
                            }

                            if (education.getFieldOfStudy() == null) {
                                education.setFieldOfStudy("");
                            }

                            if (education.getInstitution() == null) {
                                education.setInstitution("");
                            }

                            if (education.getLocation() == null) {
                                education.setLocation("");
                            }

                            if (education.getStartDate() == null) {
                                education.setStartDate("");
                            }

                            if (education.getEndDate() == null) {
                                education.setEndDate("");
                            }

                            if (education.getGrade() == null) {
                                education.setGrade("");
                            }
                        }
                );


        // -----------------------------------------------------
        // NORMALIZE PROJECTS
        // -----------------------------------------------------

        resume.getProjects()
                .forEach(
                        project -> {

                            if (project.getTitle() == null) {
                                project.setTitle("");
                            }

                            if (project.getDescription() == null) {
                                project.setDescription("");
                            }

                            if (project.getTechnologies() == null) {

                                project.setTechnologies(
                                        new ArrayList<>()
                                );
                            }

                            if (project.getBulletPoints() == null) {

                                project.setBulletPoints(
                                        new ArrayList<>()
                                );
                            }
                        }
                );
    }


    // =========================================================
    // VALIDATE INPUT
    // =========================================================

    private void validateInput(
            String originalCvText,
            String jobDescription) {

        if (originalCvText == null ||
                originalCvText.isBlank()) {

            throw new IllegalArgumentException(
                    "Original CV cannot be empty."
            );
        }


        if (jobDescription == null ||
                jobDescription.isBlank()) {

            throw new IllegalArgumentException(
                    "Job description cannot be empty."
            );
        }
    }
}