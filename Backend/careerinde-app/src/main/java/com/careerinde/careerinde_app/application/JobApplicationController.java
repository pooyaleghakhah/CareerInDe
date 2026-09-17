package com.careerinde.careerinde_app.application;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.careerinde.careerinde_app.user.User;
import com.careerinde.careerinde_app.user.UserRepository;

@Controller
public class JobApplicationController {

    private final JobApplicationService applicationService;
    private final UserRepository userRepository;


    public JobApplicationController(
            JobApplicationService applicationService,
            UserRepository userRepository) {

        this.applicationService =
                applicationService;

        this.userRepository =
                userRepository;
    }


    // =========================================================
    // CURRENT USER
    // =========================================================

    private User getCurrentUser(
            Authentication authentication) {

        return userRepository
                .findByEmail(
                        authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"));
    }


    // =========================================================
    // APPLICATION LIST
    // =========================================================

    @GetMapping("/applications")
    public String applicationsPage(
            Authentication authentication,
            Model model) {

        User user =
                getCurrentUser(authentication);

        model.addAttribute(
                "applications",
                applicationService
                        .getApplications(user));

        model.addAttribute(
                "totalApplications",
                applicationService
                        .countAll(user));

        model.addAttribute(
                "appliedCount",
                applicationService
                        .countByStatus(
                                user,
                                ApplicationStatus.APPLIED));

        model.addAttribute(
                "interviewCount",
                applicationService
                        .countByStatus(
                                user,
                                ApplicationStatus.INTERVIEW));

        model.addAttribute(
                "offerCount",
                applicationService
                        .countByStatus(
                                user,
                                ApplicationStatus.OFFER));

        return "applications";
    }


    // =========================================================
    // ADD
    // =========================================================

    @GetMapping("/applications/add")
    public String addApplicationPage(
            Model model) {

        JobApplication application =
                new JobApplication();

        application.setStatus(
                ApplicationStatus.SAVED);

        model.addAttribute(
                "jobApplication",
                application);

        model.addAttribute(
                "statuses",
                ApplicationStatus.values());

        return "add-application";
    }


    @PostMapping("/applications/add")
    public String saveApplication(
            @ModelAttribute
            JobApplication jobApplication,
            Authentication authentication) {

        User user =
                getCurrentUser(authentication);

        applicationService
                .createApplication(
                        jobApplication,
                        user);

        return "redirect:/applications";
    }


    // =========================================================
    // DETAIL
    // =========================================================

    @GetMapping("/applications/{id}")
    public String applicationDetails(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        User user =
                getCurrentUser(authentication);

        JobApplication application =
                applicationService
                        .getApplication(
                                id,
                                user);

        model.addAttribute(
                "application",
                application);

        model.addAttribute(
                "statuses",
                ApplicationStatus.values());

        return "application-detail";
    }


    // =========================================================
    // EDIT
    // =========================================================

    @GetMapping("/applications/{id}/edit")
    public String editApplicationPage(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        User user =
                getCurrentUser(authentication);

        model.addAttribute(
                "jobApplication",
                applicationService
                        .getApplication(
                                id,
                                user));

        model.addAttribute(
                "statuses",
                ApplicationStatus.values());

        return "edit-application";
    }


    @PostMapping("/applications/{id}/edit")
    public String updateApplication(
            @PathVariable Long id,
            @ModelAttribute
            JobApplication jobApplication,
            Authentication authentication) {

        User user =
                getCurrentUser(authentication);

        applicationService
                .updateApplication(
                        id,
                        jobApplication,
                        user);

        return "redirect:/applications/" + id;
    }


    // =========================================================
    // STATUS
    // =========================================================

    @PostMapping("/applications/{id}/status")
    public String changeStatus(
            @PathVariable Long id,
            @RequestParam
            ApplicationStatus status,
            Authentication authentication) {

        User user =
                getCurrentUser(authentication);

        applicationService
                .changeStatus(
                        id,
                        status,
                        user);

        return "redirect:/applications/" + id;
    }


    // =========================================================
    // DELETE
    // =========================================================

    @PostMapping("/applications/{id}/delete")
    public String deleteApplication(
            @PathVariable Long id,
            Authentication authentication) {

        User user =
                getCurrentUser(authentication);

        applicationService
                .deleteApplication(
                        id,
                        user);

        return "redirect:/applications";
    }
}