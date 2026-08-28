package com.careerinde.careerinde_app.job.matching.scoring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class JobMatchScoringService {

    // =========================================================
    // SKILL DICTIONARY
    // =========================================================

    private static final List<String> SKILLS = List.of(

            // Backend
            "java",
            "spring",
            "spring boot",
            "spring security",
            "hibernate",
            "jpa",
            "rest",
            "rest api",
            "microservices",

            // Frontend
            "javascript",
            "typescript",
            "html",
            "css",
            "angular",
            "react",
            "vue",

            // Python / Data
            "python",
            "pandas",
            "numpy",
            "scikit-learn",
            "machine learning",
            "data analysis",
            "data analytics",
            "data science",

            // BI
            "power bi",
            "dax",
            "tableau",
            "business intelligence",

            // Database
            "sql",
            "mysql",
            "postgresql",
            "mongodb",
            "redis",

            // DevOps / Cloud
            "docker",
            "kubernetes",
            "aws",
            "azure",
            "gcp",
            "git",
            "github",
            "gitlab",
            "ci/cd",

            // Development
            "maven",
            "gradle",
            "linux",

            // Project / Business
            "project management",
            "agile",
            "scrum",
            "jira",
            "process optimization",

            // AI
            "artificial intelligence",
            "künstliche intelligenz",
            "llm",
            "generative ai",
            "gemini",
            "openai"
    );


    // =========================================================
    // STOP WORDS
    // =========================================================

    /*
     * Important:
     *
     * Set.of(...) does NOT allow duplicate values.
     *
     * Therefore every value in this list must be unique.
     */
    private static final Set<String> STOP_WORDS =
            Set.of(

                    // English
                    "and",
                    "or",
                    "the",
                    "a",
                    "an",
                    "of",
                    "to",
                    "in",
                    "for",
                    "with",
                    "on",
                    "at",
                    "is",
                    "are",
                    "be",
                    "as",
                    "we",
                    "you",
                    "your",
                    "our",
                    "this",
                    "that",

                    // German
                    "und",
                    "oder",
                    "der",
                    "die",
                    "das",
                    "ein",
                    "eine",
                    "einer",
                    "einen",
                    "einem",
                    "eines",
                    "von",
                    "zu",
                    "mit",
                    "für",
                    "im",
                    "auf",
                    "als",
                    "ist",
                    "sind",
                    "wir",
                    "sie",
                    "ihre",
                    "ihr"
            );


    // =========================================================
    // PUBLIC API
    // =========================================================

    public JobMatchScore calculateScore(
            String cvText,
            String jobDescription) {

        validateInput(
                cvText,
                jobDescription
        );


        String normalizedCv =
                normalize(
                        cvText
                );


        String normalizedJob =
                normalize(
                        jobDescription
                );


        // -----------------------------------------------------
        // SKILLS
        // -----------------------------------------------------

        List<String> requiredSkills =
                extractSkills(
                        normalizedJob
                );


        List<String> candidateSkills =
                extractSkills(
                        normalizedCv
                );


        List<String> matchedSkills =
                findMatchedSkills(
                        candidateSkills,
                        requiredSkills
                );


        List<String> missingSkills =
                findMissingSkills(
                        candidateSkills,
                        requiredSkills
                );


        // -----------------------------------------------------
        // SCORES
        // -----------------------------------------------------

        int skillScore =
                calculateSkillScore(
                        requiredSkills,
                        matchedSkills
                );


        int keywordScore =
                calculateKeywordScore(
                        normalizedCv,
                        normalizedJob
                );


        int experienceScore =
                calculateExperienceScore(
                        normalizedCv,
                        normalizedJob
                );


        int educationScore =
                calculateEducationScore(
                        normalizedCv,
                        normalizedJob
                );


        int overallScore =
                calculateOverallScore(
                        skillScore,
                        keywordScore,
                        experienceScore,
                        educationScore
                );


        // -----------------------------------------------------
        // RESULT
        // -----------------------------------------------------

        JobMatchScore result =
                new JobMatchScore();


        result.setSkillScore(
                skillScore
        );


        result.setKeywordScore(
                keywordScore
        );


        result.setExperienceScore(
                experienceScore
        );


        result.setEducationScore(
                educationScore
        );


        result.setOverallScore(
                overallScore
        );


        result.setMatchedSkills(
                matchedSkills
        );


        result.setMissingSkills(
                missingSkills
        );


        printDebug(
                result,
                requiredSkills
        );


        return result;
    }


    // =========================================================
    // SKILL EXTRACTION
    // =========================================================

    private List<String> extractSkills(
            String text) {

        Set<String> result =
                new LinkedHashSet<>();


        /*
         * Longer phrases are checked first.
         *
         * Example:
         *
         * spring boot
         *
         * should be detected before:
         *
         * spring
         */
        SKILLS.stream()

                .sorted(
                        (first, second) ->
                                Integer.compare(
                                        second.length(),
                                        first.length()
                                )
                )

                .forEach(
                        skill -> {

                            if (containsPhrase(
                                    text,
                                    skill)) {

                                result.add(
                                        skill
                                );
                            }
                        }
                );


        return new ArrayList<>(
                result
        );
    }


    // =========================================================
    // MATCHED SKILLS
    // =========================================================

    private List<String> findMatchedSkills(
            List<String> candidateSkills,
            List<String> requiredSkills) {

        List<String> result =
                new ArrayList<>();


        for (String requiredSkill :
                requiredSkills) {

            if (candidateSkills.contains(
                    requiredSkill)) {

                result.add(
                        requiredSkill
                );
            }
        }


        return result;
    }


    // =========================================================
    // MISSING SKILLS
    // =========================================================

    private List<String> findMissingSkills(
            List<String> candidateSkills,
            List<String> requiredSkills) {

        List<String> result =
                new ArrayList<>();


        for (String requiredSkill :
                requiredSkills) {

            if (!candidateSkills.contains(
                    requiredSkill)) {

                result.add(
                        requiredSkill
                );
            }
        }


        return result;
    }


    // =========================================================
    // SKILL SCORE
    // =========================================================

    private int calculateSkillScore(
            List<String> requiredSkills,
            List<String> matchedSkills) {

        /*
         * If no known skill is detected in the job description,
         * return a neutral score.
         *
         * We intentionally do not return 100.
         */
        if (requiredSkills.isEmpty()) {

            return 50;
        }


        double ratio =
                (double) matchedSkills.size()
                        /
                        requiredSkills.size();


        int score =
                (int) Math.round(
                        ratio * 100
                );


        return clamp(
                score
        );
    }


    // =========================================================
    // KEYWORD SCORE
    // =========================================================

    private int calculateKeywordScore(
            String cvText,
            String jobDescription) {

        Set<String> jobKeywords =
                extractKeywords(
                        jobDescription
                );


        if (jobKeywords.isEmpty()) {

            return 50;
        }


        Set<String> cvKeywords =
                extractKeywords(
                        cvText
                );


        int matched =
                0;


        for (String keyword :
                jobKeywords) {

            if (cvKeywords.contains(
                    keyword)) {

                matched++;
            }
        }


        double ratio =
                (double) matched
                        /
                        jobKeywords.size();


        int score =
                (int) Math.round(
                        ratio * 100
                );


        return clamp(
                score
        );
    }


    // =========================================================
    // KEYWORD EXTRACTION
    // =========================================================

    private Set<String> extractKeywords(
            String text) {

        Set<String> result =
                new LinkedHashSet<>();


        String[] words =
                text.split(
                        "\\s+"
                );


        Arrays.stream(
                        words
                )

                .map(
                        this::cleanWord
                )

                .filter(
                        word ->
                                word.length() >= 4
                )

                .filter(
                        word ->
                                !STOP_WORDS.contains(
                                        word
                                )
                )

                .forEach(
                        result::add
                );


        return result;
    }


    // =========================================================
    // EXPERIENCE SCORE
    // =========================================================

    private int calculateExperienceScore(
            String cvText,
            String jobDescription) {

        int requestedYears =
                extractRequestedYears(
                        jobDescription
                );


        /*
         * No explicit experience requirement detected.
         */
        if (requestedYears == 0) {

            return 70;
        }


        int candidateYears =
                estimateCandidateYears(
                        cvText
                );


        /*
         * Job requires experience,
         * but we could not verify explicit years in CV.
         */
        if (candidateYears <= 0) {

            return 30;
        }


        double ratio =
                (double) candidateYears
                        /
                        requestedYears;


        if (ratio >= 1.0) {

            return 100;
        }


        int score =
                (int) Math.round(
                        ratio * 100
                );


        return clamp(
                score
        );
    }


    // =========================================================
    // REQUESTED EXPERIENCE
    // =========================================================

    private int extractRequestedYears(
            String jobDescription) {

        /*
         * MVP implementation.
         *
         * Examples:
         *
         * 3 years
         * 3+ years
         * 3 Jahre
         * 3+ Jahre
         */

        for (int years = 10;
             years >= 1;
             years--) {

            List<String> patterns =
                    List.of(

                            years + " years",
                            years + "+ years",

                            years + " year",
                            years + "+ year",

                            years + " jahre",
                            years + "+ jahre",

                            years + " jahr",
                            years + "+ jahr"
                    );


            for (String pattern :
                    patterns) {

                if (jobDescription.contains(
                        pattern)) {

                    return years;
                }
            }
        }


        return 0;
    }


    // =========================================================
    // CANDIDATE EXPERIENCE
    // =========================================================

    private int estimateCandidateYears(
            String cvText) {

        /*
         * Conservative MVP implementation.
         *
         * We only accept explicit statements such as:
         *
         * 5 years of experience
         * 5+ years experience
         * 5 Jahre Erfahrung
         *
         * Later this should be replaced with date-based
         * experience calculation.
         */

        for (int years = 20;
             years >= 1;
             years--) {

            List<String> patterns =
                    List.of(

                            years + " years of experience",
                            years + "+ years of experience",

                            years + " years experience",
                            years + "+ years experience",

                            years + " jahre erfahrung",
                            years + "+ jahre erfahrung"
                    );


            for (String pattern :
                    patterns) {

                if (cvText.contains(
                        pattern)) {

                    return years;
                }
            }
        }


        return 0;
    }


    // =========================================================
    // EDUCATION SCORE
    // =========================================================

    private int calculateEducationScore(
            String cvText,
            String jobDescription) {

        boolean requiresMaster =
                containsAny(
                        jobDescription,

                        "master",
                        "master's",
                        "master degree",
                        "masterabschluss",
                        "masterstudium"
                );


        boolean requiresBachelor =
                containsAny(
                        jobDescription,

                        "bachelor",
                        "bachelor's",
                        "bachelor degree",
                        "bachelorabschluss",
                        "bachelorstudium"
                );


        boolean candidateHasMaster =
                containsAny(
                        cvText,

                        "master",
                        "master of science",
                        "master of arts",
                        "m.sc",
                        "msc"
                );


        boolean candidateHasBachelor =
                containsAny(
                        cvText,

                        "bachelor",
                        "bachelor of science",
                        "bachelor of arts",
                        "b.sc",
                        "bsc"
                );


        // -----------------------------------------------------
        // MASTER REQUIRED
        // -----------------------------------------------------

        if (requiresMaster) {

            if (candidateHasMaster) {

                return 100;
            }


            if (candidateHasBachelor) {

                return 65;
            }


            return 30;
        }


        // -----------------------------------------------------
        // BACHELOR REQUIRED
        // -----------------------------------------------------

        if (requiresBachelor) {

            if (candidateHasMaster ||
                    candidateHasBachelor) {

                return 100;
            }


            return 30;
        }


        /*
         * No explicit degree requirement detected.
         */
        return 80;
    }


    // =========================================================
    // FINAL WEIGHTED SCORE
    // =========================================================

    private int calculateOverallScore(
            int skillScore,
            int keywordScore,
            int experienceScore,
            int educationScore) {

        /*
         * CareerInDe MVP Match Formula
         *
         * Skills       = 45%
         * Keywords     = 25%
         * Experience   = 20%
         * Education    = 10%
         *
         * Total        = 100%
         */

        double score =
                (skillScore * 0.45)
                        +
                        (keywordScore * 0.25)
                        +
                        (experienceScore * 0.20)
                        +
                        (educationScore * 0.10);


        return clamp(
                (int) Math.round(
                        score
                )
        );
    }


    // =========================================================
    // PHRASE MATCHING
    // =========================================================

    private boolean containsPhrase(
            String text,
            String phrase) {

        if (text == null ||
                phrase == null) {

            return false;
        }


        return text.contains(
                phrase
        );
    }


    // =========================================================
    // CONTAINS ANY
    // =========================================================

    private boolean containsAny(
            String text,
            String... values) {

        if (text == null ||
                values == null) {

            return false;
        }


        for (String value :
                values) {

            if (value != null &&
                    text.contains(
                            value
                    )) {

                return true;
            }
        }


        return false;
    }


    // =========================================================
    // NORMALIZATION
    // =========================================================

    private String normalize(
            String text) {

        if (text == null) {

            return "";
        }


        return text
                .toLowerCase(
                        Locale.ROOT
                )

                .replace(
                        "\u0000",
                        " "
                )

                .replaceAll(
                        "[\\r\\n\\t]+",
                        " "
                )

                .replaceAll(
                        "\\s+",
                        " "
                )

                .trim();
    }


    // =========================================================
    // CLEAN WORD
    // =========================================================

    private String cleanWord(
            String word) {

        if (word == null) {

            return "";
        }


        return word
                .toLowerCase(
                        Locale.ROOT
                )

                .replaceAll(
                        "^[^\\p{L}\\p{N}+#.]+",
                        ""
                )

                .replaceAll(
                        "[^\\p{L}\\p{N}+#.]+$",
                        ""
                );
    }


    // =========================================================
    // SCORE LIMIT
    // =========================================================

    private int clamp(
            int score) {

        return Math.max(
                0,
                Math.min(
                        100,
                        score
                )
        );
    }


    // =========================================================
    // INPUT VALIDATION
    // =========================================================

    private void validateInput(
            String cvText,
            String jobDescription) {

        if (cvText == null ||
                cvText.isBlank()) {

            throw new IllegalArgumentException(
                    "CV text cannot be empty."
            );
        }


        if (jobDescription == null ||
                jobDescription.isBlank()) {

            throw new IllegalArgumentException(
                    "Job description cannot be empty."
            );
        }
    }


    // =========================================================
    // DEBUG
    // =========================================================

    private void printDebug(
            JobMatchScore score,
            List<String> requiredSkills) {

        System.out.println();

        System.out.println(
                "======================================"
        );


        System.out.println(
                "CAREERINDE MATCH ENGINE"
        );


        System.out.println(
                "Required skills: "
                        + requiredSkills
        );


        System.out.println(
                "Matched skills: "
                        + score.getMatchedSkills()
        );


        System.out.println(
                "Missing skills: "
                        + score.getMissingSkills()
        );


        System.out.println(
                "Skill score: "
                        + score.getSkillScore()
                        + "%"
        );


        System.out.println(
                "Keyword score: "
                        + score.getKeywordScore()
                        + "%"
        );


        System.out.println(
                "Experience score: "
                        + score.getExperienceScore()
                        + "%"
        );


        System.out.println(
                "Education score: "
                        + score.getEducationScore()
                        + "%"
        );


        System.out.println(
                "OVERALL MATCH: "
                        + score.getOverallScore()
                        + "%"
        );


        System.out.println(
                "======================================"
        );
    }
}