package com.careerinde.careerinde_app.resume;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.careerinde.careerinde_app.ai.CVOptimizationService;

@Controller
public class CVOptimizationController {

    private final CVOptimizationService cvOptimizationService;


    public CVOptimizationController(
            CVOptimizationService cvOptimizationService) {

        this.cvOptimizationService = cvOptimizationService;
    }


    @PostMapping("/resume/optimize")
    public String optimizeCV(
            @RequestParam("cvText") String cvText,
            Model model) {

        // Check CV text
        if (cvText == null || cvText.isBlank()) {

            model.addAttribute(
                    "error",
                    "CV text is empty. Please upload your CV again."
            );

            return "cv-optimized";
        }


        // Send original CV to AI optimization service
        String optimizedCV =
                cvOptimizationService.optimizeCV(cvText);


        // Send results to Thymeleaf
        model.addAttribute(
                "optimizedCV",
                optimizedCV
        );

        model.addAttribute(
                "originalCV",
                cvText
        );


        return "cv-optimized";
    }
}