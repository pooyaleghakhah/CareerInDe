package com.careerinde.careerinde_app.admin;

import com.careerinde.careerinde_app.application.JobApplicationRepository;
import com.careerinde.careerinde_app.report.AIReportRepository;
import com.careerinde.careerinde_app.security.jwt.JwtService;
import com.careerinde.careerinde_app.user.UserRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final AIReportRepository aiReportRepository;
    private final JwtService jwtService;

    public AdminController(
            UserRepository userRepository,
            JobApplicationRepository jobApplicationRepository,
            AIReportRepository aiReportRepository,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.aiReportRepository = aiReportRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model) {

        model.addAttribute(
                "totalUsers",
                userRepository.count());

        model.addAttribute(
                "totalApplications",
                jobApplicationRepository.count());

        model.addAttribute(
                "totalReports",
                aiReportRepository.count());

        return "admin-dashboard";
    }

    @GetMapping("/test123")
    public String test123() {

        System.out.println("ADMIN CONTROLLER LOADED");

        return "index";
    }

    @GetMapping("/token-test")
    @ResponseBody
    public String tokenTest() {

        return jwtService.generateToken(
                "admin@careerinde.de");
    }
}