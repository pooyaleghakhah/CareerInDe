package com.careerinde.careerinde_app.job.matching;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.careerinde.careerinde_app.job.JobResult;
import com.careerinde.careerinde_app.job.JobService;
import com.careerinde.careerinde_app.profile.Profile;

@Service
public class RecommendedJobService {

    private final JobService jobService;

    private final CandidateProfileBuilder
            candidateProfileBuilder;

    private final CohereJobMatchingService
            cohereJobMatchingService;

    private final CareerInDeMatchScoringService
            scoringService;


    public RecommendedJobService(

            JobService jobService,

            CandidateProfileBuilder
                    candidateProfileBuilder,

            CohereJobMatchingService
                    cohereJobMatchingService,

            CareerInDeMatchScoringService
                    scoringService) {


        this.jobService =
                jobService;


        this.candidateProfileBuilder =
                candidateProfileBuilder;


        this.cohereJobMatchingService =
                cohereJobMatchingService;


        this.scoringService =
                scoringService;
    }


    public List<RecommendedJob> findRecommendedJobs(
            Profile profile,
            int topN) {


        if (profile == null) {

            throw new IllegalArgumentException(
                    "Profile cannot be null."
            );
        }


        // =====================================================
        // TARGET JOB
        // =====================================================

        String targetJob =
                profile.getTargetJob();


        if (targetJob == null ||
                targetJob.isBlank()) {

            throw new IllegalStateException(
                    "Please add a target job to your profile."
            );
        }


        // =====================================================
        // LOCATION
        // =====================================================

        String location =
                profile.getTargetCity();


        if (location == null ||
                location.isBlank()) {

            location =
                    profile.getCity();
        }


        // =====================================================
        // 1. LOAD REAL JOBS
        // =====================================================

        List<JobResult> jobs =
                jobService.searchJobs(
                        targetJob,
                        location
                );


        if (jobs == null ||
                jobs.isEmpty()) {

            return List.of();
        }


        // =====================================================
        // 2. BUILD CANDIDATE PROFILE
        // =====================================================

        String candidateProfile =
                candidateProfileBuilder.build(
                        profile
                );


        // =====================================================
        // 3. COHERE SEMANTIC RERANK
        // =====================================================

        List<CohereRerankResult> reranked =
                cohereJobMatchingService
                        .rerankJobs(
                                candidateProfile,
                                jobs,
                                topN
                        );


        if (reranked == null ||
                reranked.isEmpty()) {

            return List.of();
        }


        // =====================================================
        // 4. CAREERINDE SCORE
        // =====================================================

        List<RecommendedJob> recommendations =
                new ArrayList<>();


        for (CohereRerankResult result : reranked) {


            int index =
                    result.getIndex();


            if (index < 0 ||
                    index >= jobs.size()) {

                continue;
            }


            JobResult job =
                    jobs.get(index);


            CareerInDeMatchScore matchScore =
                    scoringService.calculate(
                            profile,
                            job,
                            result.getRelevanceScore()
                    );


            RecommendedJob recommendation =
                    new RecommendedJob(
                            job,
                            result.getRelevanceScore(),
                            matchScore
                    );


            recommendations.add(
                    recommendation
            );
        }


        // =====================================================
        // 5. SORT BY CAREERINDE SCORE
        // =====================================================

        recommendations.sort(

                Comparator
                        .comparingInt(

                                (RecommendedJob item) ->

                                        item.getMatchScore()
                                                .getOverallScore()

                        )
                        .reversed()

        );


        return recommendations;
    }
}