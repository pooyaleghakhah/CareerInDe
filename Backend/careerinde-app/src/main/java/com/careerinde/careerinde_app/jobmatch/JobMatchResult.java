package com.careerinde.careerinde_app.jobmatch;

import java.util.List;

public class JobMatchResult {

    private int matchScore;

    private String matchLevel;

    private int skillsMatch;

    private int experienceMatch;

    private int educationMatch;

    private List<String> matchingSkills;

    private List<String> missingSkills;

    private String hiringProbability;

    private List<String> recommendations;


    // =========================
    // Empty constructor
    // Required by Jackson
    // =========================

    public JobMatchResult() {
    }


    // =========================
    // Match Score
    // =========================

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }


    // =========================
    // Match Level
    // =========================

    public String getMatchLevel() {
        return matchLevel;
    }

    public void setMatchLevel(String matchLevel) {
        this.matchLevel = matchLevel;
    }


    // =========================
    // Skills Match
    // =========================

    public int getSkillsMatch() {
        return skillsMatch;
    }

    public void setSkillsMatch(int skillsMatch) {
        this.skillsMatch = skillsMatch;
    }


    // =========================
    // Experience Match
    // =========================

    public int getExperienceMatch() {
        return experienceMatch;
    }

    public void setExperienceMatch(int experienceMatch) {
        this.experienceMatch = experienceMatch;
    }


    // =========================
    // Education Match
    // =========================

    public int getEducationMatch() {
        return educationMatch;
    }

    public void setEducationMatch(int educationMatch) {
        this.educationMatch = educationMatch;
    }


    // =========================
    // Matching Skills
    // =========================

    public List<String> getMatchingSkills() {
        return matchingSkills;
    }

    public void setMatchingSkills(List<String> matchingSkills) {
        this.matchingSkills = matchingSkills;
    }


    // =========================
    // Missing Skills
    // =========================

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }


    // =========================
    // Hiring Probability
    // =========================

    public String getHiringProbability() {
        return hiringProbability;
    }

    public void setHiringProbability(String hiringProbability) {
        this.hiringProbability = hiringProbability;
    }


    // =========================
    // Recommendations
    // =========================

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
}