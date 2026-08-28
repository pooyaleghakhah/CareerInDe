package com.careerinde.careerinde_app.job.matching;

import com.careerinde.careerinde_app.job.JobResult;

public class RecommendedJob {

    private JobResult job;

    /*
     * Raw semantic relevance returned by Cohere.
     *
     * Example:
     * 0.91
     *
     * This is NOT the final CareerInDe Match Score.
     */
    private double relevanceScore;


    /*
     * CareerInDe deterministic score.
     */
    private CareerInDeMatchScore matchScore;


    public RecommendedJob() {
    }


    public RecommendedJob(
            JobResult job,
            double relevanceScore) {

        this.job = job;

        this.relevanceScore =
                relevanceScore;
    }


    public RecommendedJob(
            JobResult job,
            double relevanceScore,
            CareerInDeMatchScore matchScore) {

        this.job = job;

        this.relevanceScore =
                relevanceScore;

        this.matchScore =
                matchScore;
    }


    public JobResult getJob() {
        return job;
    }


    public void setJob(
            JobResult job) {

        this.job = job;
    }


    public double getRelevanceScore() {
        return relevanceScore;
    }


    public void setRelevanceScore(
            double relevanceScore) {

        this.relevanceScore =
                relevanceScore;
    }


    public CareerInDeMatchScore getMatchScore() {
        return matchScore;
    }


    public void setMatchScore(
            CareerInDeMatchScore matchScore) {

        this.matchScore =
                matchScore;
    }
}