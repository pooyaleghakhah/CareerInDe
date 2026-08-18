package com.careerinde.careerinde_app.profile;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.careerinde.careerinde_app.user.User;
import com.careerinde.careerinde_app.user.UserRepository;

@Controller
public class ProfilePageController {

    private final ProfileService profileService;
    private final UserRepository userRepository;

    public ProfilePageController(
            ProfileService profileService,
            UserRepository userRepository) {

        this.profileService = profileService;
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String showProfile(
            Model model,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Profile profile =
                profileService.getProfileOrEmpty(user);

        model.addAttribute(
                "profile",
                profile
        );

        return "profile";
    }

    @PostMapping("/profile")
    public String saveProfile(
            Profile formProfile,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        profileService.saveProfileForUser(
                user,
                formProfile
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Profile saved successfully."
        );

        return "redirect:/profile";
    }
}