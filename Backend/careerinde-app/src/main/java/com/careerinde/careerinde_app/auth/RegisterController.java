package com.careerinde.careerinde_app.auth;

import java.util.Locale;

import com.careerinde.careerinde_app.auth.email.EmailService;
import com.careerinde.careerinde_app.auth.verification.EmailVerificationService;
import com.careerinde.careerinde_app.auth.verification.EmailVerificationToken;
import com.careerinde.careerinde_app.user.User;
import com.careerinde.careerinde_app.user.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService verificationService;
    private final EmailService emailService;

    public RegisterController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailVerificationService verificationService,
            EmailService emailService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationService = verificationService;
        this.emailService = emailService;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {

        model.addAttribute("user", new User());

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute User user,
            Model model) {

        String email = user.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {

            model.addAttribute(
                    "errorMessage",
                    "This email is already registered."
            );

            return "register";
        }

        user.setEmail(email);

        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );

        user.setRole("USER");

        // User cannot login before email verification
        user.setEnabled(false);

        User savedUser =
                userRepository.save(user);

        EmailVerificationToken verificationToken =
                verificationService
                        .createVerificationToken(savedUser);

        emailService.sendVerificationEmail(
                savedUser.getEmail(),
                verificationToken.getToken()
        );

        model.addAttribute(
                "email",
                savedUser.getEmail()
        );

        return "check-email";
    }
}