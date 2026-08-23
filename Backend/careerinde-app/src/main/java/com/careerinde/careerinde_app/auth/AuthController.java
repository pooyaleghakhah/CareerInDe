package com.careerinde.careerinde_app.auth;

import com.careerinde.careerinde_app.auth.dto.AuthResponse;
import com.careerinde.careerinde_app.auth.dto.LoginRequest;
import com.careerinde.careerinde_app.security.jwt.JwtService;
import com.careerinde.careerinde_app.user.User;
import com.careerinde.careerinde_app.user.UserRepository;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            JwtService jwtService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid email or password");
        }

        String token =
                jwtService.generateToken(
                        user.getEmail());

        return new AuthResponse(token);
    }
}
