package com.careerinde.careerinde_app.AIAnalysisResult;

import java.util.ArrayList;
import java.util.List;

public class AIAnalysisResult {

    private int atsScore;

    private String profileLevel;

    private String bestJobMatch;

    private List<String> strengths = new ArrayList<>();

    private List<String> missingSkills = new ArrayList<>();

    private List<String> recommendations = new ArrayList<>();


    // Empty constructor
    public AIAnalysisResult() {
    }


    // ATS Score

    public int getAtsScore() {
        return atsScore;
    }

    public void setAtsScore(int atsScore) {
        this.atsScore = atsScore;
    }


    // Profile Level

    public String getProfileLevel() {
        return profileLevel;
    }

    public void setProfileLevel(String profileLevel) {
        this.profileLevel = profileLevel;
    }


    // Best Job Match

    public String getBestJobMatch() {
        return bestJobMatch;
    }

    public void setBestJobMatch(String bestJobMatch) {
        this.bestJobMatch = bestJobMatch;
    }


    // Strengths

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }


    // Missing Skills

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }


    // Recommendations

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(
            List<String> recommendations) {

        this.recommendations = recommendations;
    }
}