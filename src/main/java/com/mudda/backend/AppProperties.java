/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : AppProperties
 * Author  : Vikas Kumar
 * Created : 15-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String frontendBaseUrl;
    private Token token;
    private Security security;
    private RateLimit rateLimit;

    @Getter
    @Setter
    public static class Token {
        private int resendCooldownMinutes;
        private int verificationExpiryHours;
    }

    @Getter
    @Setter
    public static class Security {
        private int maxAttempts;
        private int lockDurationMinutes;
    }

    @Getter
    @Setter
    public static class RateLimit {
        private int capacity;
        private int refillTokens;
        private int refillMinutes;
    }
}
