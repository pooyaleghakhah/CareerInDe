package com.careerinde.careerinde_app.auth;

import java.util.Locale;

import com.careerinde.careerinde_app.auth.email.EmailService;
import com.careerinde.careerinde_app.auth.security.DisposableEmailService;
import com.careerinde.careerinde_app.auth.security.RegistrationRateLimitService;
import com.careerinde.careerinde_app.auth.security.TrustedEmailDomainService;
import com.careerinde.careerinde_app.auth.verification.EmailVerificationService;
import com.careerinde.careerinde_app.auth.verification.EmailVerificationToken;
import com.careerinde.careerinde_app.user.User;
import com.careerinde.careerinde_app.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

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

    private final RegistrationRateLimitService rateLimitService;
    private final DisposableEmailService disposableEmailService;
    private final TrustedEmailDomainService trustedEmailDomainService;


    public RegisterController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailVerificationService verificationService,
            EmailService emailService,
            RegistrationRateLimitService rateLimitService,
            DisposableEmailService disposableEmailService,
            TrustedEmailDomainService trustedEmailDomainService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationService = verificationService;
        this.emailService = emailService;

        this.rateLimitService = rateLimitService;
        this.disposableEmailService = disposableEmailService;
        this.trustedEmailDomainService = trustedEmailDomainService;
    }


    /*
     * =============================================
     * REGISTER PAGE
     * =============================================
     */

    @GetMapping("/register")
    public String showRegisterPage(Model model) {

        model.addAttribute(
                "user",
                new User()
        );

        return "register";
    }


    /*
     * =============================================
     * REGISTER USER
     * =============================================
     */

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute User user,
            HttpServletRequest request,
            Model model) {

        /*
         * -----------------------------------------
         * 1. CLIENT IP
         * -----------------------------------------
         */

        String clientIp =
                getClientIp(request);


        /*
         * -----------------------------------------
         * 2. RATE LIMIT
         * -----------------------------------------
         */

        if (!rateLimitService.isAllowed(clientIp)) {

            model.addAttribute(
                    "errorMessage",
                    "Too many registration attempts. "
                    + "Please try again in 15 minutes."
            );

            return "register";
        }


        /*
         * -----------------------------------------
         * 3. EMAIL
         * -----------------------------------------
         */

        if (user.getEmail() == null
                || user.getEmail().isBlank()) {

            model.addAttribute(
                    "errorMessage",
                    "Please enter a valid email address."
            );

            return "register";
        }


        String email =
                user.getEmail()
                        .trim()
                        .toLowerCase(Locale.ROOT);


        /*
         * -----------------------------------------
         * 4. DISPOSABLE EMAIL
         * -----------------------------------------
         */

        if (disposableEmailService.isDisposable(email)) {

            System.out.println(
                    "REGISTRATION BLOCKED - DISPOSABLE EMAIL"
            );

            System.out.println(
                    "IP: " + clientIp
            );

            model.addAttribute(
                    "errorMessage",
                    "Temporary or disposable email addresses "
                    + "are not allowed."
            );

            return "register";
        }


        /*
         * -----------------------------------------
         * 5. EMAIL DOMAIN / MX
         * -----------------------------------------
         */

        if (!trustedEmailDomainService
                .isValidEmailDomain(email)) {

            System.out.println(
                    "REGISTRATION BLOCKED - INVALID EMAIL DOMAIN"
            );

            System.out.println(
                    "Domain: "
                    + trustedEmailDomainService
                            .extractDomain(email)
            );

            System.out.println(
                    "IP: " + clientIp
            );

            model.addAttribute(
                    "errorMessage",
                    "We could not verify this email domain. "
                    + "Please use a valid personal, company "
                    + "or university email address."
            );

            return "register";
        }


        /*
         * -----------------------------------------
         * 6. DUPLICATE EMAIL
         * -----------------------------------------
         */

        if (userRepository.existsByEmail(email)) {

            model.addAttribute(
                    "errorMessage",
                    "This email is already registered."
            );

            return "register";
        }


        /*
         * -----------------------------------------
         * 7. PASSWORD
         * -----------------------------------------
         */

        if (user.getPassword() == null
                || user.getPassword().length() < 8) {

            model.addAttribute(
                    "errorMessage",
                    "Password must contain at least 8 characters."
            );

            return "register";
        }


        /*
         * -----------------------------------------
         * 8. PREPARE USER
         * -----------------------------------------
         */

        user.setEmail(email);

        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );

        user.setRole("USER");

        /*
         * Email verification is still mandatory.
         */
        user.setEnabled(false);


        /*
         * -----------------------------------------
         * 9. SAVE
         * -----------------------------------------
         */

        User savedUser =
                userRepository.save(user);


        /*
         * -----------------------------------------
         * 10. VERIFICATION TOKEN
         * -----------------------------------------
         */

        EmailVerificationToken verificationToken =
                verificationService
                        .createVerificationToken(savedUser);


        /*
         * -----------------------------------------
         * 11. VERIFICATION EMAIL
         * -----------------------------------------
         */

        emailService.sendVerificationEmail(
                savedUser.getEmail(),
                verificationToken.getToken()
        );


        /*
         * -----------------------------------------
         * 12. SUCCESS
         * -----------------------------------------
         */

        System.out.println(
                "CAREERINDE REGISTRATION SUCCESS"
        );

        System.out.println(
                "Email domain: "
                + trustedEmailDomainService
                        .extractDomain(email)
        );

        System.out.println(
                "IP: " + clientIp
        );


        model.addAttribute(
                "email",
                savedUser.getEmail()
        );

        return "check-email";
    }


    /*
     * =============================================
     * CLIENT IP
     * =============================================
     */

    private String getClientIp(
            HttpServletRequest request) {

        String forwardedFor =
                request.getHeader(
                        "X-Forwarded-For"
                );

        if (forwardedFor != null
                && !forwardedFor.isBlank()) {

            return forwardedFor
                    .split(",")[0]
                    .trim();
        }

        return request.getRemoteAddr();
    }
}