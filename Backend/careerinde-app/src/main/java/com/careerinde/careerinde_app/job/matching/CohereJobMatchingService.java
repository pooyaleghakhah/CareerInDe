package com.careerinde.careerinde_app.job.matching;

import com.careerinde.careerinde_app.job.JobResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CohereJobMatchingService {

    private static final String COHERE_RERANK_URL =
            "https://api.cohere.com/v2/rerank";

    private static final String MODEL =
            "rerank-v3.5";

    private final RestClient restClient;

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    private final String apiKey;

    public CohereJobMatchingService(
            @Value("${cohere.api.key:}")
            String apiKey) {

        this.apiKey = apiKey;

        this.restClient =
                RestClient.builder()
                        .baseUrl(
                                COHERE_RERANK_URL
                        )
                        .defaultHeader(
                                HttpHeaders.CONTENT_TYPE,
                                MediaType.APPLICATION_JSON_VALUE
                        )
                        .build();
    }

    public List<CohereRerankResult> rerankJobs(
            String candidateProfile,
            List<JobResult> jobs,
            int topN) {

        if (candidateProfile == null ||
                candidateProfile.isBlank()) {

            throw new IllegalArgumentException(
                    "Candidate profile cannot be empty."
            );
        }

        if (jobs == null ||
                jobs.isEmpty()) {

            return List.of();
        }

        if (apiKey == null ||
                apiKey.isBlank()) {

            throw new IllegalStateException(
                    "COHERE_API_KEY is not configured."
            );
        }

        List<String> documents =
                new ArrayList<>();

        for (JobResult job : jobs) {

            documents.add(
                    buildJobDocument(job)
            );
        }

        int safeTopN =
                Math.min(
                        Math.max(
                                topN,
                                1
                        ),
                        documents.size()
                );

        Map<String, Object> requestBody =
                new HashMap<>();

        requestBody.put(
                "model",
                MODEL
        );

        requestBody.put(
                "query",
                candidateProfile
        );

        requestBody.put(
                "documents",
                documents
        );

        requestBody.put(
                "top_n",
                safeTopN
        );

        try {

            String response =
                    restClient
                            .post()
                            .header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer " + apiKey
                            )
                            .body(
                                    requestBody
                            )
                            .retrieve()
                            .body(
                                    String.class
                            );

            if (response == null ||
                    response.isBlank()) {

                return List.of();
            }

            JsonNode root =
                    objectMapper.readTree(
                            response
                    );

            JsonNode results =
                    root.get(
                            "results"
                    );

            if (results == null ||
                    !results.isArray()) {

                return List.of();
            }

            List<CohereRerankResult> matches =
                    new ArrayList<>();

            for (JsonNode result : results) {

                int index =
                        result
                                .path("index")
                                .asInt();

                double relevanceScore =
                        result
                                .path(
                                        "relevance_score"
                                )
                                .asDouble();

                if (index < 0 ||
                        index >= jobs.size()) {

                    continue;
                }

                matches.add(
                        new CohereRerankResult(
                                index,
                                relevanceScore
                        )
                );
            }

            return matches;

        } catch (Exception exception) {

            System.err.println(
                    "===== COHERE RERANK ERROR ====="
            );

            System.err.println(
                    exception.getMessage()
            );

            System.err.println(
                    "==============================="
            );

            throw new RuntimeException(
                    "Unable to rerank jobs with Cohere.",
                    exception
            );
        }
    }

    private String buildJobDocument(
            JobResult job) {

        StringBuilder document =
                new StringBuilder();

        appendField(
                document,
                "Job title",
                job.getTitle()
        );

        appendField(
                document,
                "Company",
                job.getCompany()
        );

        appendField(
                document,
                "Location",
                job.getLocation()
        );

        appendField(
                document,
                "Employment type",
                job.getType()
        );

        appendField(
                document,
                "Description",
                job.getDescription()
        );

        return document.toString();
    }

    private void appendField(
            StringBuilder builder,
            String label,
            String value) {

        if (value == null ||
                value.isBlank()) {

            return;
        }

        builder
                .append(label)
                .append(": ")
                .append(
                        value.trim()
                )
                .append("\n");
    }
}