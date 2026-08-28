package com.careerinde.careerinde_app.job.matching;

import org.springframework.stereotype.Component;

import com.careerinde.careerinde_app.profile.Profile;

@Component
public class CandidateProfileBuilder {

    public String build(Profile profile) {

        if (profile == null) {
            throw new IllegalArgumentException(
                    "Profile cannot be null."
            );
        }

        StringBuilder builder =
                new StringBuilder();


        appendField(
                builder,
                "Target job",
                profile.getTargetJob()
        );

        appendField(
                builder,
                "Target city",
                profile.getTargetCity()
        );

        appendField(
                builder,
                "Current city",
                profile.getCity()
        );

        appendField(
                builder,
                "Country",
                profile.getCountry()
        );

        appendField(
                builder,
                "Experience level",
                profile.getExperienceLevel()
        );

        if (profile.getExperienceYears() != null) {

            appendField(
                    builder,
                    "Years of experience",
                    String.valueOf(
                            profile.getExperienceYears()
                    )
            );
        }

        appendField(
                builder,
                "Skills",
                profile.getSkills()
        );

        appendField(
                builder,
                "German level",
                profile.getGermanLevel()
        );

        appendField(
                builder,
                "English level",
                profile.getEnglishLevel()
        );

        appendField(
                builder,
                "Preferred work mode",
                profile.getPreferredWorkMode()
        );

        if (profile.getWillingToRelocate() != null) {

            appendField(
                    builder,
                    "Willing to relocate",
                    profile.getWillingToRelocate()
                            ? "Yes"
                            : "No"
            );
        }

        if (profile.getSalaryExpectation() != null) {

            appendField(
                    builder,
                    "Salary expectation",
                    String.valueOf(
                            profile.getSalaryExpectation()
                    )
            );
        }

        appendField(
                builder,
                "About candidate",
                profile.getAboutMe()
        );


        String candidateProfile =
                builder
                        .toString()
                        .trim();


        if (candidateProfile.isBlank()) {

            throw new IllegalStateException(
                    "Candidate profile does not contain enough information."
            );
        }


        return candidateProfile;
    }


    private void appendField(
            StringBuilder builder,
            String label,
            String value) {

        if (value == null ||
                value.isBlank()) {

            return;
        }

        builder
                .append(label)
                .append(": ")
                .append(value.trim())
                .append("\n");
    }
}