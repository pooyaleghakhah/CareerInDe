package com.careerinde.careerinde_app.resume.optimization;

import java.util.ArrayList;
import java.util.List;

public class OptimizedProject {

    private String title;

    private String description;

    private List<String> technologies =
            new ArrayList<>();

    private List<String> bulletPoints =
            new ArrayList<>();


    public OptimizedProject() {
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(
            String title) {

        this.title = title;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(
            String description) {

        this.description = description;
    }


    public List<String> getTechnologies() {
        return technologies;
    }


    public void setTechnologies(
            List<String> technologies) {

        this.technologies =
                technologies != null
                        ? technologies
                        : new ArrayList<>();
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