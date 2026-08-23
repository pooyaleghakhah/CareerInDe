package com.careerinde.careerinde_app.auth.verification;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.careerinde.careerinde_app.auth.verification.EmailVerificationService.VerificationResult;

@Controller
public class EmailVerificationController {

    private final EmailVerificationService verificationService;

    public EmailVerificationController(
            EmailVerificationService verificationService) {

        this.verificationService = verificationService;
    }

    @GetMapping("/verify-email")
    public String verifyEmail(
            @RequestParam("token") String token,
            Model model) {

        VerificationResult result =
                verificationService.verifyToken(token);

        switch (result) {

            case SUCCESS -> {

                model.addAttribute(
                        "success",
                        true
                );

                model.addAttribute(
                        "message",
                        "Your email has been verified successfully."
                );
            }

            case EXPIRED -> {

                model.addAttribute(
                        "success",
                        false
                );

                model.addAttribute(
                        "message",
                        "Your verification link has expired."
                );
            }

            case ALREADY_VERIFIED -> {

                model.addAttribute(
                        "success",
                        true
                );

                model.addAttribute(
                        "message",
                        "Your email has already been verified."
                );
            }

            case INVALID -> {

                model.addAttribute(
                        "success",
                        false
                );

                model.addAttribute(
                        "message",
                        "The verification link is invalid."
                );
            }
        }

        return "verification-result";
    }
}