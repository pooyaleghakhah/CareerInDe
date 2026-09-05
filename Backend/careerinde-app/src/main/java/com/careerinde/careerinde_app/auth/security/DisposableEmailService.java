package com.careerinde.careerinde_app.auth.security;

import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class DisposableEmailService {

    /*
     * Known disposable / temporary email providers.
     *
     * This is an MVP protection layer.
     * Later we can replace or extend this with a maintained
     * disposable-domain database or external validation service.
     */
    private static final Set<String> BLOCKED_DOMAINS = Set.of(

            "10minutemail.com",
            "10minutemail.net",

            "mailinator.com",
            "mailinator.net",

            "guerrillamail.com",
            "guerrillamail.net",
            "guerrillamail.org",

            "yopmail.com",
            "yopmail.fr",
            "yopmail.net",

            "temp-mail.org",
            "tempmail.com",
            "tempmail.net",

            "throwawaymail.com",
            "throwawaymail.net",

            "getnada.com",

            "maildrop.cc",

            "dispostable.com",

            "fakeinbox.com",

            "trashmail.com",
            "trashmail.net",

            "emailondeck.com",

            "mohmal.com",

            "mintemail.com",

            "sharklasers.com",

            "spam4.me"
    );


    public boolean isDisposable(String email) {

        if (email == null || email.isBlank()) {
            return false;
        }

        String normalizedEmail =
                email.trim().toLowerCase(Locale.ROOT);

        int atIndex =
                normalizedEmail.lastIndexOf("@");

        if (atIndex <= 0
                || atIndex == normalizedEmail.length() - 1) {

            return false;
        }

        String domain =
                normalizedEmail.substring(atIndex + 1);

        return isBlockedDomain(domain);
    }


    private boolean isBlockedDomain(String domain) {

        if (BLOCKED_DOMAINS.contains(domain)) {
            return true;
        }

        /*
         * Also block subdomains of known disposable providers.
         *
         * Example:
         * something.mailinator.com
         */
        for (String blockedDomain : BLOCKED_DOMAINS) {

            if (domain.endsWith("." + blockedDomain)) {
                return true;
            }
        }

        return false;
    }
}