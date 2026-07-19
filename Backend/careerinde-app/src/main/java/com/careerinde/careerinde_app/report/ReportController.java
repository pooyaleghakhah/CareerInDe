package com.careerinde.careerinde_app.report;

import java.util.List;

import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;

import com.careerinde.careerinde_app.user.User;
import com.careerinde.careerinde_app.user.UserRepository;

@Controller
public class ReportController {

    private final AIReportRepository
            aiReportRepository;

    private final UserRepository
            userRepository;

    public ReportController(

            AIReportRepository aiReportRepository,

            UserRepository userRepository) {

        this.aiReportRepository =
                aiReportRepository;

        this.userRepository =
                userRepository;
    }

    @GetMapping("/reports")
    public String reportsPage(

            Authentication authentication,

            Model model) {

        String email =
                authentication.getName();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        List<AIReport> reports =
                aiReportRepository
                        .findByUser(user);

        model.addAttribute(
                "reports",
                reports);

        return "reports";
    }
}