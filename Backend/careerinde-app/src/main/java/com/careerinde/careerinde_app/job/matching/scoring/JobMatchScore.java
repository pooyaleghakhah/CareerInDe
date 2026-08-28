package com.careerinde.careerinde_app.job.matching.scoring;

import java.util.ArrayList;
import java.util.List;

public class JobMatchScore {

    private int overallScore;

    private int skillScore;
    private int keywordScore;
    private int experienceScore;
    private int educationScore;

    private List<String> matchedSkills = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();


    public JobMatchScore() {
    }


    public int getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(int overallScore) {
        this.overallScore = overallScore;
    }


    public int getSkillScore() {
        return skillScore;
    }

    public void setSkillScore(int skillScore) {
        this.skillScore = skillScore;
    }


    public int getKeywordScore() {
        return keywordScore;
    }

    public void setKeywordScore(int keywordScore) {
        this.keywordScore = keywordScore;
    }


    public int getExperienceScore() {
        return experienceScore;
    }

    public void setExperienceScore(int experienceScore) {
        this.experienceScore = experienceScore;
    }


    public int getEducationScore() {
        return educationScore;
    }

    public void setEducationScore(int educationScore) {
        this.educationScore = educationScore;
    }


    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }


    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }


    @Override
    public String toString() {

        return "JobMatchScore{" +
                "overallScore=" + overallScore +
                ", skillScore=" + skillScore +
                ", keywordScore=" + keywordScore +
                ", experienceScore=" + experienceScore +
                ", educationScore=" + educationScore +
                ", matchedSkills=" + matchedSkills +
                ", missingSkills=" + missingSkills +
                '}';
    }
}