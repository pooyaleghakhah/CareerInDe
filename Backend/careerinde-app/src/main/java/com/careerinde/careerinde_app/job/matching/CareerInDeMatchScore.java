package com.careerinde.careerinde_app.job.matching;

public class CareerInDeMatchScore {

    private int overallScore;

    private int semanticScore;

    private int roleScore;

    private int locationScore;

    private int experienceScore;

    private int languageScore;


    public CareerInDeMatchScore() {
    }


    public CareerInDeMatchScore(
            int overallScore,
            int semanticScore,
            int roleScore,
            int locationScore,
            int experienceScore,
            int languageScore) {

        this.overallScore = overallScore;

        this.semanticScore = semanticScore;

        this.roleScore = roleScore;

        this.locationScore = locationScore;

        this.experienceScore = experienceScore;

        this.languageScore = languageScore;
    }


    public int getOverallScore() {
        return overallScore;
    }


    public void setOverallScore(
            int overallScore) {

        this.overallScore = overallScore;
    }


    public int getSemanticScore() {
        return semanticScore;
    }


    public void setSemanticScore(
            int semanticScore) {

        this.semanticScore = semanticScore;
    }


    public int getRoleScore() {
        return roleScore;
    }


    public void setRoleScore(
            int roleScore) {

        this.roleScore = roleScore;
    }


    public int getLocationScore() {
        return locationScore;
    }


    public void setLocationScore(
            int locationScore) {

        this.locationScore = locationScore;
    }


    public int getExperienceScore() {
        return experienceScore;
    }


    public void setExperienceScore(
            int experienceScore) {

        this.experienceScore = experienceScore;
    }


    public int getLanguageScore() {
        return languageScore;
    }


    public void setLanguageScore(
            int languageScore) {

        this.languageScore = languageScore;
    }
}