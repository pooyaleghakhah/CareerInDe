package com.careerinde.careerinde_app.auth.security;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.springframework.stereotype.Service;

@Service
public class TrustedEmailDomainService {

    /*
     * Well-known public email providers.
     */
    private static final Set<String> TRUSTED_PUBLIC_DOMAINS = Set.of(

            // Google
            "gmail.com",
            "googlemail.com",

            // Microsoft
            "outlook.com",
            "hotmail.com",
            "live.com",
            "msn.com",

            // Yahoo
            "yahoo.com",
            "yahoo.de",

            // Apple
            "icloud.com",
            "me.com",
            "mac.com",

            // Germany
            "gmx.de",
            "gmx.net",
            "web.de",
            "t-online.de",
            "freenet.de",

            // Proton
            "proton.me",
            "protonmail.com",

            // Other established providers
            "aol.com",
            "mail.com"
    );


    /*
     * =============================================
     * MAIN VALIDATION
     * =============================================
     */

    public boolean isValidEmailDomain(String email) {

        String domain = extractDomain(email);

        if (domain == null) {
            return false;
        }

        /*
         * Trusted providers do not need DNS lookup.
         */
        if (TRUSTED_PUBLIC_DOMAINS.contains(domain)) {
            return true;
        }

        /*
         * Custom domain must first have
         * a valid domain structure.
         */
        if (!hasValidDomainFormat(domain)) {
            return false;
        }

        /*
         * Company / university / custom domain:
         * verify that it actually has an MX record.
         */
        return hasMxRecord(domain);
    }


    /*
     * =============================================
     * TRUSTED PUBLIC PROVIDER
     * =============================================
     */

    public boolean isTrustedPublicProvider(String email) {

        String domain = extractDomain(email);

        if (domain == null) {
            return false;
        }

        return TRUSTED_PUBLIC_DOMAINS.contains(domain);
    }


    /*
     * =============================================
     * DOMAIN FORMAT
     * =============================================
     */

    private boolean hasValidDomainFormat(String domain) {

        if (domain == null || domain.isBlank()) {
            return false;
        }

        if (domain.length() > 253) {
            return false;
        }

        if (!domain.contains(".")) {
            return false;
        }

        if (domain.startsWith(".")
                || domain.endsWith(".")) {

            return false;
        }

        if (domain.contains("..")) {
            return false;
        }

        return domain.matches(
                "^[a-z0-9](?:[a-z0-9.-]*[a-z0-9])?\\.[a-z]{2,}$"
        );
    }


    /*
     * =============================================
     * MX RECORD CHECK
     * =============================================
     */

    public boolean hasMxRecord(String domain) {

        if (domain == null || domain.isBlank()) {
            return false;
        }

        DirContext context = null;

        try {

            Hashtable<String, String> environment =
                    new Hashtable<>();

            environment.put(
                    Context.INITIAL_CONTEXT_FACTORY,
                    "com.sun.jndi.dns.DnsContextFactory"
            );

            environment.put(
                    "com.sun.jndi.dns.timeout.initial",
                    "3000"
            );

            environment.put(
                    "com.sun.jndi.dns.timeout.retries",
                    "1"
            );

            context =
                    new InitialDirContext(environment);

            Attributes attributes =
                    context.getAttributes(
                            domain,
                            new String[]{"MX"}
                    );

            Attribute mx =
                    attributes.get("MX");

            return mx != null
                    && mx.size() > 0;

        } catch (NamingException exception) {

            System.out.println(
                    "EMAIL DOMAIN MX CHECK FAILED"
            );

            System.out.println(
                    "Domain: " + domain
            );

            return false;

        } finally {

            if (context != null) {

                try {
                    context.close();
                } catch (NamingException ignored) {
                }
            }
        }
    }


    /*
     * =============================================
     * DOMAIN EXTRACTION
     * =============================================
     */

    public String extractDomain(String email) {

        if (email == null || email.isBlank()) {
            return null;
        }

        String normalized =
                email.trim()
                        .toLowerCase(Locale.ROOT);

        int firstAt =
                normalized.indexOf("@");

        int lastAt =
                normalized.lastIndexOf("@");

        /*
         * Must contain exactly one @
         */
        if (firstAt <= 0
                || firstAt != lastAt
                || firstAt == normalized.length() - 1) {

            return null;
        }

        return normalized.substring(
                firstAt + 1
        );
    }
}