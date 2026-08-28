package com.careerinde.careerinde_app.resume.optimization;

import java.util.ArrayList;
import java.util.List;

public class OptimizedResume {

    private String fullName;
    private String targetRole;
    private String email;
    private String phone;
    private String location;
    private String linkedin;
    private String github;

    private String professionalSummary;

    private List<String> skills = new ArrayList<>();
    private List<OptimizedExperience> experiences = new ArrayList<>();
    private List<OptimizedEducation> education = new ArrayList<>();
    private List<OptimizedProject> projects = new ArrayList<>();
    private List<String> languages = new ArrayList<>();
    private List<String> certifications = new ArrayList<>();

    private int originalMatchScore;
    private int optimizedMatchScore;

    public OptimizedResume() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getProfessionalSummary() {
        return professionalSummary;
    }

    public void setProfessionalSummary(String professionalSummary) {
        this.professionalSummary = professionalSummary;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<OptimizedExperience> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<OptimizedExperience> experiences) {
        this.experiences = experiences;
    }

    public List<OptimizedEducation> getEducation() {
        return education;
    }

    public void setEducation(List<OptimizedEducation> education) {
        this.education = education;
    }

    public List<OptimizedProject> getProjects() {
        return projects;
    }

    public void setProjects(List<OptimizedProject> projects) {
        this.projects = projects;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<String> certifications) {
        this.certifications = certifications;
    }

    public int getOriginalMatchScore() {
        return originalMatchScore;
    }

    public void setOriginalMatchScore(int originalMatchScore) {
        this.originalMatchScore = originalMatchScore;
    }

    public int getOptimizedMatchScore() {
        return optimizedMatchScore;
    }

    public void setOptimizedMatchScore(int optimizedMatchScore) {
        this.optimizedMatchScore = optimizedMatchScore;
    }
}