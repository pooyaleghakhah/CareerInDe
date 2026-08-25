package com.careerinde.careerinde_app.auth.email;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class EmailService {

    private final WebClient webClient;

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.mail.from}")
    private String fromEmail;

    public EmailService() {

        this.webClient = WebClient.builder()
                .baseUrl("https://api.resend.com")
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    // =========================================================
    // EMAIL VERIFICATION
    // =========================================================

    public void sendVerificationEmail(
            String recipientEmail,
            String token) {

        String verificationUrl =
                baseUrl
                        + "/verify-email?token="
                        + token;

        String html = """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;">
                    <h2>Welcome to CareerInDe</h2>

                    <p>
                        Please verify your email address to activate your account.
                    </p>

                    <p style="margin:30px 0;">
                        <a href="%s"
                           style="
                               background:#2563eb;
                               color:#ffffff;
                               text-decoration:none;
                               padding:14px 24px;
                               border-radius:8px;
                               display:inline-block;
                               font-weight:bold;
                           ">
                            Verify Email
                        </a>
                    </p>

                    <p>
                        This verification link expires in 30 minutes.
                    </p>

                    <p>
                        If you did not create a CareerInDe account,
                        you can ignore this email.
                    </p>

                    <hr>

                    <p style="color:#777;font-size:13px;">
                        CareerInDe · Career Intelligence for Germany
                    </p>
                </div>
                """.formatted(verificationUrl);

        sendEmail(
                recipientEmail,
                "Verify your CareerInDe email",
                html
        );
    }

    // =========================================================
    // PASSWORD RESET
    // =========================================================

    public void sendPasswordResetEmail(
            String recipientEmail,
            String token) {

        String resetUrl =
                baseUrl
                        + "/reset-password?token="
                        + token;

        String html = """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;">

                    <h2>Reset your CareerInDe password</h2>

                    <p>
                        We received a request to reset the password
                        for your CareerInDe account.
                    </p>

                    <p style="margin:30px 0;">
                        <a href="%s"
                           style="
                               background:#2563eb;
                               color:#ffffff;
                               text-decoration:none;
                               padding:14px 24px;
                               border-radius:8px;
                               display:inline-block;
                               font-weight:bold;
                           ">
                            Reset Password
                        </a>
                    </p>

                    <p>
                        This password reset link expires in 30 minutes.
                    </p>

                    <p>
                        If you did not request a password reset,
                        you can safely ignore this email.
                    </p>

                    <p>
                        For security reasons, this link can only
                        be used once.
                    </p>

                    <hr>

                    <p style="color:#777;font-size:13px;">
                        CareerInDe · Career Intelligence for Germany
                    </p>

                </div>
                """.formatted(resetUrl);

        sendEmail(
                recipientEmail,
                "Reset your CareerInDe password",
                html
        );
    }

    // =========================================================
    // SEND EMAIL THROUGH RESEND
    // =========================================================

    private void sendEmail(
            String recipientEmail,
            String subject,
            String html) {

        Map<String, Object> requestBody =
                Map.of(
                        "from",
                        fromEmail,

                        "to",
                        new String[]{
                                recipientEmail
                        },

                        "subject",
                        subject,

                        "html",
                        html
                );

        try {

            webClient.post()
                    .uri("/emails")
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            "Bearer " + resendApiKey
                    )
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println(
                    "Email sent successfully to: "
                            + recipientEmail
            );

        } catch (WebClientResponseException e) {

            System.err.println(
                    "===== RESEND API ERROR ====="
            );

            System.err.println(
                    "Status: "
                            + e.getStatusCode()
            );

            System.err.println(
                    "Response: "
                            + e.getResponseBodyAsString()
            );

            System.err.println(
                    "============================"
            );

            throw e;

        } catch (Exception e) {

            System.err.println(
                    "Email sending failed: "
                            + e.getMessage()
            );

            throw e;
        }
    }
}