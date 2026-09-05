package com.careerinde.careerinde_app.auth.security;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class RegistrationRateLimitService {

    private static final int MAX_ATTEMPTS = 5;

    private static final Duration WINDOW =
            Duration.ofMinutes(15);

    private final Map<String, Deque<Instant>> attemptsByIp =
            new ConcurrentHashMap<>();


    public boolean isAllowed(String ipAddress) {

        if (ipAddress == null || ipAddress.isBlank()) {
            ipAddress = "unknown";
        }

        Instant now = Instant.now();
        Instant windowStart = now.minus(WINDOW);

        Deque<Instant> attempts =
                attemptsByIp.computeIfAbsent(
                        ipAddress,
                        key -> new ArrayDeque<>()
                );

        synchronized (attempts) {

            while (!attempts.isEmpty()
                    && attempts.peekFirst().isBefore(windowStart)) {

                attempts.pollFirst();
            }

            if (attempts.size() >= MAX_ATTEMPTS) {

                System.out.println(
                        "REGISTRATION RATE LIMIT BLOCKED"
                );

                System.out.println(
                        "IP: " + ipAddress
                );

                return false;
            }

            attempts.addLast(now);

            return true;
        }
    }
}