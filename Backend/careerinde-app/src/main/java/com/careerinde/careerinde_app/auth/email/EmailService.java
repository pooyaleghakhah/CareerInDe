package com.careerinde.careerinde_app.auth.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(
            String recipientEmail,
            String token) {

        String verificationUrl =
                baseUrl
                + "/verify-email?token="
                + token;

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(fromEmail);

        message.setTo(recipientEmail);

        message.setSubject(
                "Verify your CareerInDe email"
        );

        message.setText(
                """
                Welcome to CareerInDe!

                Please verify your email address by opening the link below:

                %s

                This verification link expires in 30 minutes.

                If you did not create a CareerInDe account,
                you can ignore this email.

                CareerInDe
                """.formatted(verificationUrl)
        );

        mailSender.send(message);
    }
}