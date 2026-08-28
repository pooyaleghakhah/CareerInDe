package com.careerinde.careerinde_app.job.matching;

import org.springframework.stereotype.Service;

import com.careerinde.careerinde_app.job.JobResult;
import com.careerinde.careerinde_app.profile.Profile;

@Service
public class CareerInDeMatchScoringService {

    public CareerInDeMatchScore calculate(
            Profile profile,
            JobResult job,
            double cohereRelevanceScore) {


        int semanticScore =
                clamp(
                        (int) Math.round(
                                cohereRelevanceScore * 100
                        )
                );


        int roleScore =
                calculateRoleScore(
                        profile,
                        job
                );


        int locationScore =
                calculateLocationScore(
                        profile,
                        job
                );


        int experienceScore =
                calculateExperienceScore(
                        profile,
                        job
                );


        int languageScore =
                calculateLanguageScore(
                        profile,
                        job
                );


        /*
         * CareerInDe Match Score V1
         *
         * Semantic relevance = 50%
         * Role fit          = 20%
         * Location fit      = 15%
         * Experience fit    = 10%
         * Language fit      =  5%
         */

        int overallScore =
                (int) Math.round(

                        semanticScore * 0.50

                        +

                        roleScore * 0.20

                        +

                        locationScore * 0.15

                        +

                        experienceScore * 0.10

                        +

                        languageScore * 0.05

                );


        return new CareerInDeMatchScore(
                clamp(overallScore),
                semanticScore,
                roleScore,
                locationScore,
                experienceScore,
                languageScore
        );
    }


    // =========================================================
    // ROLE SCORE
    // =========================================================

    private int calculateRoleScore(
            Profile profile,
            JobResult job) {


        if (profile == null ||
                profile.getTargetJob() == null ||
                profile.getTargetJob().isBlank()) {

            return 60;
        }


        if (job == null ||
                job.getTitle() == null ||
                job.getTitle().isBlank()) {

            return 50;
        }


        String target =
                normalize(
                        profile.getTargetJob()
                );


        String title =
                normalize(
                        job.getTitle()
                );


        if (title.equals(target)) {
            return 100;
        }


        if (title.contains(target) ||
                target.contains(title)) {

            return 95;
        }


        String[] targetWords =
                target.split("\\s+");


        int matches = 0;
        int relevantWords = 0;


        for (String word : targetWords) {

            if (word.length() < 3) {
                continue;
            }


            relevantWords++;


            if (title.contains(word)) {
                matches++;
            }
        }


        if (relevantWords == 0) {
            return 60;
        }


        double ratio =
                (double) matches
                        /
                relevantWords;


        if (ratio >= 0.75) {
            return 90;
        }


        if (ratio >= 0.50) {
            return 80;
        }


        if (ratio > 0) {
            return 65;
        }


        return 40;
    }


    // =========================================================
    // LOCATION SCORE
    // =========================================================

    private int calculateLocationScore(
            Profile profile,
            JobResult job) {


        if (job == null ||
                job.getLocation() == null ||
                job.getLocation().isBlank()) {

            return 60;
        }


        String targetCity =
                profile.getTargetCity();


        if (targetCity == null ||
                targetCity.isBlank()) {

            targetCity =
                    profile.getCity();
        }


        if (targetCity == null ||
                targetCity.isBlank()) {

            return 60;
        }


        String wanted =
                normalize(
                        targetCity
                );


        String actual =
                normalize(
                        job.getLocation()
                );


        if (actual.contains(wanted)) {
            return 100;
        }


        if (actual.contains("remote") ||
                actual.contains("homeoffice") ||
                actual.contains("home office")) {

            return 90;
        }


        if (actual.contains("germany") ||
                actual.contains("deutschland")) {

            return 70;
        }


        return 40;
    }


    // =========================================================
    // EXPERIENCE SCORE
    // =========================================================

    private int calculateExperienceScore(
            Profile profile,
            JobResult job) {


        Integer years =
                profile.getExperienceYears();


        String experienceLevel =
                normalize(
                        profile.getExperienceLevel()
                );


        if (job == null) {
            return 60;
        }


        String jobText =
                normalize(

                        safe(
                                job.getTitle()
                        )

                        + " "

                        + safe(
                                job.getDescription()
                        )
                );


        /*
         * Senior / Lead / Principal
         */

        if (jobText.contains("senior") ||
                jobText.contains("lead") ||
                jobText.contains("principal")) {


            if (years != null) {

                if (years >= 5) {
                    return 100;
                }


                if (years >= 3) {
                    return 75;
                }


                return 35;
            }


            if (experienceLevel.contains("senior")) {
                return 95;
            }


            if (experienceLevel.contains("mid")) {
                return 70;
            }


            return 40;
        }


        /*
         * Junior / Graduate / Entry
         */

        if (jobText.contains("junior") ||
                jobText.contains("entry level") ||
                jobText.contains("entry-level") ||
                jobText.contains("graduate") ||
                jobText.contains("trainee")) {


            if (years != null) {

                if (years <= 3) {
                    return 100;
                }


                return 80;
            }


            if (experienceLevel.contains("junior") ||
                    experienceLevel.contains("entry") ||
                    experienceLevel.contains("student")) {

                return 100;
            }


            return 80;
        }


        /*
         * Normal role
         */

        if (years == null) {

            if (experienceLevel.contains("junior")) {
                return 75;
            }


            if (experienceLevel.contains("mid")) {
                return 90;
            }


            if (experienceLevel.contains("senior")) {
                return 90;
            }


            return 65;
        }


        if (years >= 2) {
            return 90;
        }


        if (years == 1) {
            return 75;
        }


        return 65;
    }


    // =========================================================
    // LANGUAGE SCORE
    // =========================================================

    private int calculateLanguageScore(
            Profile profile,
            JobResult job) {


        if (job == null) {
            return 70;
        }


        String jobText =
                normalize(

                        safe(
                                job.getTitle()
                        )

                        + " "

                        + safe(
                                job.getDescription()
                        )
                );


        if (jobText.isBlank()) {
            return 70;
        }


        boolean requiresGerman =
                jobText.contains("german")
                        ||
                jobText.contains("deutsch");


        boolean requiresEnglish =
                jobText.contains("english")
                        ||
                jobText.contains("englisch");


        if (!requiresGerman &&
                !requiresEnglish) {

            return 80;
        }


        int score = 0;

        int checks = 0;


        if (requiresGerman) {

            checks++;

            score +=
                    languageLevelScore(
                            profile.getGermanLevel()
                    );
        }


        if (requiresEnglish) {

            checks++;

            score +=
                    languageLevelScore(
                            profile.getEnglishLevel()
                    );
        }


        if (checks == 0) {
            return 80;
        }


        return clamp(
                score / checks
        );
    }


    // =========================================================
    // LANGUAGE LEVEL
    // =========================================================

    private int languageLevelScore(
            String level) {


        if (level == null ||
                level.isBlank()) {

            return 40;
        }


        String normalized =
                normalize(
                        level
                );


        if (normalized.contains("native")) {
            return 100;
        }


        if (normalized.contains("c2")) {
            return 100;
        }


        if (normalized.contains("c1")) {
            return 95;
        }


        if (normalized.contains("b2")) {
            return 85;
        }


        if (normalized.contains("b1")) {
            return 70;
        }


        if (normalized.contains("a2")) {
            return 50;
        }


        if (normalized.contains("a1")) {
            return 35;
        }


        return 60;
    }


    // =========================================================
    // HELPERS
    // =========================================================

    private String normalize(
            String value) {


        if (value == null) {
            return "";
        }


        return value
                .toLowerCase()
                .trim();
    }


    private String safe(
            String value) {


        return value == null
                ? ""
                : value;
    }


    private int clamp(
            int value) {


        return Math.max(
                0,
                Math.min(
                        value,
                        100
                )
        );
    }
}