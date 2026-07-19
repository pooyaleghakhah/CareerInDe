package com.careerinde.careerinde_app.application;

import java.time.LocalDate;

import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.careerinde.careerinde_app.user.User;
import com.careerinde.careerinde_app.user.UserRepository;

@Controller
public class JobApplicationController {

    private final JobApplicationRepository repository;

    private final UserRepository userRepository;

    public JobApplicationController(

            JobApplicationRepository repository,

            UserRepository userRepository) {

        this.repository =
                repository;

        this.userRepository =
                userRepository;
    }

    @GetMapping("/applications")
    public String applicationsPage(

            Authentication authentication,

            Model model) {

        String email =
                authentication.getName();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow();

        model.addAttribute(
                "applications",

                repository.findByUser(user));

        return "applications";
    }

    @GetMapping("/applications/add")
    public String addApplicationPage(
            Model model) {

        model.addAttribute(
                "jobApplication",

                new JobApplication());

        return "add-application";
    }

    @PostMapping("/applications/add")
    public String saveApplication(

            @ModelAttribute
            JobApplication jobApplication,

            Authentication authentication) {

        String email =
                authentication.getName();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow();

        jobApplication.setUser(user);

        if (jobApplication
                .getApplicationDate() == null) {

            jobApplication.setApplicationDate(
                    LocalDate.now());
        }

        repository.save(jobApplication);

        return "redirect:/applications";
    }
}