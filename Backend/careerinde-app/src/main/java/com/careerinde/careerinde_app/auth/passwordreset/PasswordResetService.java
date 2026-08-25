package com.careerinde.careerinde_app.auth.passwordreset;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.careerinde.careerinde_app.user.User;
import com.careerinde.careerinde_app.user.UserRepository;

@Service
public class PasswordResetService {

    private static final long TOKEN_VALIDITY_MINUTES = 30;

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(
            PasswordResetTokenRepository tokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    // Create reset token
    // =========================================================

    @Transactional
    public PasswordResetToken createResetToken(User user) {

        tokenRepository
                .findByUser(user)
                .ifPresent(tokenRepository::delete);

        String tokenValue =
                UUID.randomUUID().toString();

        LocalDateTime expiresAt =
                LocalDateTime.now()
                        .plusMinutes(TOKEN_VALIDITY_MINUTES);

        PasswordResetToken token =
                new PasswordResetToken(
                        tokenValue,
                        user,
                        expiresAt
                );

        return tokenRepository.save(token);
    }

    // =========================================================
    // Validate token
    // =========================================================

    public ResetTokenStatus validateToken(String tokenValue) {

        PasswordResetToken token =
                tokenRepository
                        .findByToken(tokenValue)
                        .orElse(null);

        if (token == null) {
            return ResetTokenStatus.INVALID;
        }

        if (token.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            return ResetTokenStatus.EXPIRED;
        }

        return ResetTokenStatus.VALID;
    }

    // =========================================================
    // Reset password
    // =========================================================

    @Transactional
    public ResetPasswordResult resetPassword(
            String tokenValue,
            String newPassword) {

        PasswordResetToken token =
                tokenRepository
                        .findByToken(tokenValue)
                        .orElse(null);

        if (token == null) {
            return ResetPasswordResult.INVALID_TOKEN;
        }

        if (token.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            tokenRepository.delete(token);

            return ResetPasswordResult.EXPIRED_TOKEN;
        }

        User user = token.getUser();

        if (user == null) {

            tokenRepository.delete(token);

            return ResetPasswordResult.INVALID_TOKEN;
        }

        if (newPassword == null
                || newPassword.length() < 8) {

            return ResetPasswordResult.WEAK_PASSWORD;
        }

        String encodedPassword =
                passwordEncoder.encode(newPassword);

        user.setPassword(encodedPassword);

        userRepository.save(user);

        // Important:
        // Token can only be used once.
        tokenRepository.delete(token);

        return ResetPasswordResult.SUCCESS;
    }

    // =========================================================
    // Get user from reset token
    // =========================================================

    public User getUserByToken(String tokenValue) {

        PasswordResetToken token =
                tokenRepository
                        .findByToken(tokenValue)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Password reset token not found"
                                )
                        );

        return token.getUser();
    }

    // =========================================================
    // Token status
    // =========================================================

    public enum ResetTokenStatus {

        VALID,

        INVALID,

        EXPIRED
    }

    // =========================================================
    // Reset result
    // =========================================================

    public enum ResetPasswordResult {

        SUCCESS,

        INVALID_TOKEN,

        EXPIRED_TOKEN,

        WEAK_PASSWORD
    }
}