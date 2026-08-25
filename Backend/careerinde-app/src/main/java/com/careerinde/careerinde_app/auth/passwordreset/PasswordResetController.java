package com.careerinde.careerinde_app.auth.passwordreset;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.careerinde.careerinde_app.auth.email.EmailService;
import com.careerinde.careerinde_app.auth.passwordreset.PasswordResetService.ResetPasswordResult;
import com.careerinde.careerinde_app.auth.passwordreset.PasswordResetService.ResetTokenStatus;
import com.careerinde.careerinde_app.user.User;
import com.careerinde.careerinde_app.user.UserRepository;

@Controller
public class PasswordResetController {

    private final UserRepository userRepository;
    private final PasswordResetService passwordResetService;
    private final EmailService emailService;

    public PasswordResetController(
            UserRepository userRepository,
            PasswordResetService passwordResetService,
            EmailService emailService) {

        this.userRepository = userRepository;
        this.passwordResetService = passwordResetService;
        this.emailService = emailService;
    }

    // =========================================================
    // Forgot Password Page
    // =========================================================

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {

        return "forgot-password";
    }

    // =========================================================
    // Forgot Password Submit
    // =========================================================

    @PostMapping("/forgot-password")
    public String forgotPassword(
            @RequestParam("email") String email,
            Model model) {

        String normalizedEmail =
                email.trim()
                        .toLowerCase(Locale.ROOT);

        User user =
                userRepository
                        .findByEmail(normalizedEmail)
                        .orElse(null);

        /*
         * Security:
         * We do not tell the visitor whether
         * the email actually exists.
         */
        if (user != null) {

            PasswordResetToken token =
                    passwordResetService
                            .createResetToken(user);

            emailService.sendPasswordResetEmail(
                    user.getEmail(),
                    token.getToken()
            );
        }

        model.addAttribute(
                "successMessage",
                "If an account exists for this email, "
                + "a password reset link has been sent."
        );

        return "forgot-password";
    }

    // =========================================================
    // Reset Password Page
    // =========================================================

    @GetMapping("/reset-password")
    public String showResetPasswordPage(
            @RequestParam("token") String token,
            Model model) {

        ResetTokenStatus status =
                passwordResetService
                        .validateToken(token);

        if (status == ResetTokenStatus.INVALID) {

            model.addAttribute(
                    "errorMessage",
                    "This password reset link is invalid."
            );

            return "reset-password";
        }

        if (status == ResetTokenStatus.EXPIRED) {

            model.addAttribute(
                    "errorMessage",
                    "This password reset link has expired."
            );

            return "reset-password";
        }

        model.addAttribute(
                "token",
                token
        );

        model.addAttribute(
                "tokenValid",
                true
        );

        return "reset-password";
    }

    // =========================================================
    // Reset Password Submit
    // =========================================================

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        if (!password.equals(confirmPassword)) {

            model.addAttribute(
                    "token",
                    token
            );

            model.addAttribute(
                    "tokenValid",
                    true
            );

            model.addAttribute(
                    "errorMessage",
                    "Passwords do not match."
            );

            return "reset-password";
        }

        ResetPasswordResult result =
                passwordResetService
                        .resetPassword(
                                token,
                                password
                        );

        switch (result) {

            case SUCCESS -> {

                model.addAttribute(
                        "successMessage",
                        "Your password has been changed successfully."
                );

                return "reset-password-success";
            }

            case WEAK_PASSWORD -> {

                model.addAttribute(
                        "token",
                        token
                );

                model.addAttribute(
                        "tokenValid",
                        true
                );

                model.addAttribute(
                        "errorMessage",
                        "Password must contain at least 8 characters."
                );

                return "reset-password";
            }

            case EXPIRED_TOKEN -> {

                model.addAttribute(
                        "errorMessage",
                        "This password reset link has expired."
                );

                return "reset-password";
            }

            case INVALID_TOKEN -> {

                model.addAttribute(
                        "errorMessage",
                        "This password reset link is invalid."
                );

                return "reset-password";
            }

            default -> {

                model.addAttribute(
                        "errorMessage",
                        "Unable to reset password."
                );

                return "reset-password";
            }
        }
    }
}