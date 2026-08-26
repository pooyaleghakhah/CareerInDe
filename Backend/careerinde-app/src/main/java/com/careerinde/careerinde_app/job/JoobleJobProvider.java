package com.careerinde.careerinde_app.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class JoobleJobProvider implements JobProvider {

    private static final String JOOBLE_API_URL =
            "https://de.jooble.org/api/";

    private final RestClient restClient;

    @Value("${jooble.api.key}")
    private String apiKey;

    public JoobleJobProvider() {
        this.restClient = RestClient.create();
    }

    @Override
    public List<JobResult> searchJobs(
            String keywords,
            String location) {

        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("keywords", keywords);
        requestBody.put("location", location);
        requestBody.put("page", 1);

        JoobleResponse response =
                restClient
                        .post()
                        .uri(JOOBLE_API_URL + apiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .retrieve()
                        .body(JoobleResponse.class);

        List<JobResult> results =
                new ArrayList<>();

        if (response == null ||
                response.getJobs() == null) {

            return results;
        }

        for (JoobleJob job : response.getJobs()) {

            JobResult result =
                    new JobResult();

            result.setTitle(
                    job.getTitle()
            );

            result.setCompany(
                    job.getCompany()
            );

            result.setLocation(
                    job.getLocation()
            );

            result.setDescription(
                    job.getSnippet()
            );

            result.setSalary(
                    job.getSalary()
            );

            result.setType(
                    job.getType()
            );

            result.setSource(
                    "JOOBLE"
            );

            result.setLink(
                    job.getLink()
            );

            results.add(result);
        }

        return results;
    }

    @Override
    public String getProviderName() {

        return "JOOBLE";
    }

    // =========================================================
    // JOOBLE RESPONSE
    // =========================================================

    public static class JoobleResponse {

        private Integer totalCount;

        private List<JoobleJob> jobs;

        public JoobleResponse() {
        }

        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(
                Integer totalCount) {

            this.totalCount =
                    totalCount;
        }

        public List<JoobleJob> getJobs() {
            return jobs;
        }

        public void setJobs(
                List<JoobleJob> jobs) {

            this.jobs =
                    jobs;
        }
    }

    // =========================================================
    // JOOBLE JOB
    // =========================================================

    public static class JoobleJob {

        private String title;

        private String location;

        private String snippet;

        private String salary;

        private String source;

        private String type;

        private String link;

        private String company;

        private String updated;

        private String id;

        public JoobleJob() {
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(
                String title) {

            this.title =
                    title;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(
                String location) {

            this.location =
                    location;
        }

        public String getSnippet() {
            return snippet;
        }

        public void setSnippet(
                String snippet) {

            this.snippet =
                    snippet;
        }

        public String getSalary() {
            return salary;
        }

        public void setSalary(
                String salary) {

            this.salary =
                    salary;
        }

        public String getSource() {
            return source;
        }

        public void setSource(
                String source) {

            this.source =
                    source;
        }

        public String getType() {
            return type;
        }

        public void setType(
                String type) {

            this.type =
                    type;
        }

        public String getLink() {
            return link;
        }

        public void setLink(
                String link) {

            this.link =
                    link;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(
                String company) {

            this.company =
                    company;
        }

        public String getUpdated() {
            return updated;
        }

        public void setUpdated(
                String updated) {

            this.updated =
                    updated;
        }

        public String getId() {
            return id;
        }

        public void setId(
                String id) {

            this.id =
                    id;
        }
    }
}