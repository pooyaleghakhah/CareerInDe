package com.careerinde.careerinde_app.job;

import java.util.List;

public interface JobProvider {

    /**
     * Search jobs from an external job provider.
     *
     * @param keywords job title or search keywords
     * @param location target location
     * @return normalized CareerInDe job results
     */
    List<JobResult> searchJobs(
            String keywords,
            String location
    );

    /**
     * Name of the provider.
     *
     * Example:
     * JOOBLE
     * ADZUNA
     */
    String getProviderName();
}