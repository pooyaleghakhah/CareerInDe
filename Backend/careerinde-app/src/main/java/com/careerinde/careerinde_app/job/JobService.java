package com.careerinde.careerinde_app.job;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class JobService {

    private final JobProvider jobProvider;

    public JobService(JobProvider jobProvider) {
        this.jobProvider = jobProvider;
    }

    // =========================================================
    // SEARCH JOBS
    // =========================================================

    public List<JobResult> searchJobs(
            String keywords,
            String location) {

        String safeKeywords =
                keywords == null
                        ? ""
                        : keywords.trim();

        String safeLocation =
                location == null
                        ? ""
                        : location.trim();

        if (safeKeywords.isBlank()) {
            throw new IllegalArgumentException(
                    "Job keywords are required."
            );
        }

        return jobProvider.searchJobs(
                safeKeywords,
                safeLocation
        );
    }

    // =========================================================
    // PROVIDER NAME
    // =========================================================

    public String getProviderName() {
        return jobProvider.getProviderName();
    }
}