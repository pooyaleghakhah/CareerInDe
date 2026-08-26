package com.careerinde.careerinde_app.job;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // =========================================================
    // JOB SEARCH PAGE
    // =========================================================

    @GetMapping("/jobs")
    public String showJobs(
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) String location,
            Model model) {

        model.addAttribute("keywords", keywords);
        model.addAttribute("location", location);

        /*
         * User has opened /jobs but has not searched yet.
         */
        if (keywords == null || keywords.isBlank()) {

            model.addAttribute(
                    "jobs",
                    Collections.emptyList()
            );

            model.addAttribute(
                    "searched",
                    false
            );

            return "jobs";
        }

        try {

            List<JobResult> jobs =
                    jobService.searchJobs(
                            keywords,
                            location
                    );

            model.addAttribute(
                    "jobs",
                    jobs
            );

            model.addAttribute(
                    "searched",
                    true
            );

            model.addAttribute(
                    "provider",
                    jobService.getProviderName()
            );

        } catch (Exception exception) {

            model.addAttribute(
                    "jobs",
                    Collections.emptyList()
            );

            model.addAttribute(
                    "searched",
                    true
            );

            model.addAttribute(
                    "errorMessage",
                    "We could not load jobs right now. Please try again."
            );

            /*
             * Temporary during development.
             * This lets us see the real API error in the terminal.
             */
            exception.printStackTrace();
        }

        return "jobs";
    }
}