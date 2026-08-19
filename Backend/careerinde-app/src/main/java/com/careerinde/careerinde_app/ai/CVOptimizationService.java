package com.careerinde.careerinde_app.ai;

import org.springframework.stereotype.Service;

@Service
public class CVOptimizationService {

    private final OpenAIService openAIService;

    public CVOptimizationService(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }


    public String optimizeCV(String cvText) {

        String prompt = """
You are a professional CV writer specialized in the German job market.

Rewrite and optimize the following CV for applications in Germany.

IMPORTANT RULES:

- Never invent work experience.
- Never invent education.
- Never invent certifications.
- Never invent technical skills.
- Never invent companies.
- Never invent achievements or numbers.
- Never change employment dates.
- Only use information that exists in the original CV.

Your task is to improve:
- professional wording
- clarity
- structure
- ATS compatibility
- concise bullet points
- technical skill presentation
- work experience descriptions
- project descriptions
- German job market positioning

The optimized CV should be professional and suitable for
German employers and ATS systems.

Use this structure:

PROFESSIONAL SUMMARY

[Create a concise professional summary based only on the CV.]

CORE SKILLS

- skill
- skill
- skill

PROFESSIONAL EXPERIENCE

Job Title
Company
Dates

- achievement / responsibility
- achievement / responsibility
- achievement / responsibility

EDUCATION

Degree
University
Dates

PROJECTS

Project Name

- description
- technologies
- relevant contribution

TECHNICAL SKILLS

- Programming:
- Data & Analytics:
- Databases:
- Cloud:
- Tools:

LANGUAGES

- Language: Level

CERTIFICATIONS

- Certification

IMPORTANT:

Keep the CV factual.
Do not add information that cannot be found in the original CV.
If information for a section is unavailable, omit that section.

Return only the optimized CV.
Do not explain what you changed.

ORIGINAL CV:

""" + cvText;

        return openAIService.sendPrompt(prompt);
    }
}