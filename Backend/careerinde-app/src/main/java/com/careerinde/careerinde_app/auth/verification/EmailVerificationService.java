package com.careerinde.careerinde_app.auth.verification;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.careerinde.careerinde_app.user.User;
import com.careerinde.careerinde_app.user.UserRepository;

@Service
public class EmailVerificationService {

    private static final long TOKEN_VALIDITY_MINUTES = 30;

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public EmailVerificationService(
            EmailVerificationTokenRepository tokenRepository,
            UserRepository userRepository) {

        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // Create verification token
    // =========================================================

    @Transactional
    public EmailVerificationToken createVerificationToken(
            User user) {

        tokenRepository
                .findByUser(user)
                .ifPresent(tokenRepository::delete);

        String tokenValue =
                UUID.randomUUID().toString();

        LocalDateTime expiresAt =
                LocalDateTime.now()
                        .plusMinutes(
                                TOKEN_VALIDITY_MINUTES
                        );

        EmailVerificationToken token =
                new EmailVerificationToken(
                        tokenValue,
                        user,
                        expiresAt
                );

        return tokenRepository.save(token);
    }

    // =========================================================
    // Verify token
    // =========================================================

    @Transactional
    public VerificationResult verifyToken(
            String tokenValue) {

        EmailVerificationToken token =
                tokenRepository
                        .findByToken(tokenValue)
                        .orElse(null);

        if (token == null) {
            return VerificationResult.INVALID;
        }

        if (token.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            tokenRepository.delete(token);

            return VerificationResult.EXPIRED;
        }

        User user = token.getUser();

        if (user == null) {
            tokenRepository.delete(token);

            return VerificationResult.INVALID;
        }

        if (user.isEnabled()) {

            tokenRepository.delete(token);

            return VerificationResult.ALREADY_VERIFIED;
        }

        user.setEnabled(true);

        userRepository.save(user);

        tokenRepository.delete(token);

        return VerificationResult.SUCCESS;
    }

    // =========================================================
    // Resend verification token
    // =========================================================

    @Transactional
    public EmailVerificationToken regenerateToken(
            User user) {

        tokenRepository.deleteByUser(user);

        String tokenValue =
                UUID.randomUUID().toString();

        LocalDateTime expiresAt =
                LocalDateTime.now()
                        .plusMinutes(
                                TOKEN_VALIDITY_MINUTES
                        );

        EmailVerificationToken token =
                new EmailVerificationToken(
                        tokenValue,
                        user,
                        expiresAt
                );

        return tokenRepository.save(token);
    }

    // =========================================================
    // Get token by value
    // =========================================================

    public EmailVerificationToken getToken(
            String tokenValue) {

        return tokenRepository
                .findByToken(tokenValue)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Verification token not found"
                        )
                );
    }

    // =========================================================
    // Token expiration check
    // =========================================================

    public boolean isExpired(
            EmailVerificationToken token) {

        return token.getExpiresAt()
                .isBefore(LocalDateTime.now());
    }

    // =========================================================
    // Result
    // =========================================================

    public enum VerificationResult {

        SUCCESS,

        INVALID,

        EXPIRED,

        ALREADY_VERIFIED
    }
}