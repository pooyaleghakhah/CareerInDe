package com.careerinde.careerinde_app.job.matching;

public class CohereRerankResult {

    private int index;
    private double relevanceScore;

    // Required for Jackson
    public CohereRerankResult() {
    }

    public CohereRerankResult(
            int index,
            double relevanceScore) {

        this.index = index;
        this.relevanceScore = relevanceScore;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(double relevanceScore) {
        this.relevanceScore = relevanceScore;
    }
}