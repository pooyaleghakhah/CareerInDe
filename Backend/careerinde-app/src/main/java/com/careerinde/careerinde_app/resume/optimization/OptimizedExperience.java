package com.careerinde.careerinde_app.resume.optimization;

import java.util.ArrayList;
import java.util.List;

public class OptimizedExperience {

    private String jobTitle;
    private String company;
    private String location;

    private String startDate;
    private String endDate;

    private List<String> bulletPoints =
            new ArrayList<>();


    public OptimizedExperience() {
    }


    public String getJobTitle() {
        return jobTitle;
    }


    public void setJobTitle(
            String jobTitle) {

        this.jobTitle = jobTitle;
    }


    public String getCompany() {
        return company;
    }


    public void setCompany(
            String company) {

        this.company = company;
    }


    public String getLocation() {
        return location;
    }


    public void setLocation(
            String location) {

        this.location = location;
    }


    public String getStartDate() {
        return startDate;
    }


    public void setStartDate(
            String startDate) {

        this.startDate = startDate;
    }


    public String getEndDate() {
        return endDate;
    }


    public void setEndDate(
            String endDate) {

        this.endDate = endDate;
    }


    public List<String> getBulletPoints() {
        return bulletPoints;
    }


    public void setBulletPoints(
            List<String> bulletPoints) {

        this.bulletPoints =
                bulletPoints != null
                        ? bulletPoints
                        : new ArrayList<>();
    }
}