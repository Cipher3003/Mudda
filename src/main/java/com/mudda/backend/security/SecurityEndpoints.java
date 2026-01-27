/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : SecurityEndpoints
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.security;

public final class SecurityEndpoints {

    private SecurityEndpoints() {
    }

    public static final String[] SEED_ENDPOINTS = {
            "/api/v1/seed/**"
    };

    public static final String[] SWAGGER_ENDPOINTS = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v3/api-docs"
    };

    public static final String[] PUBLIC_STATIC_PAGES = {
            "/",
            "/index.html",
            "/home.html",
            "/issue.html",
            "/seed.html",
            "/media_url.html",
            "/login.html",
            "/favicon.ico",
            "/placeholder.png"
    };

    public static final String[] AUTH_PUBLIC_ENDPOINTS = {
            "/auth/register",
            "/auth/login",
            "/auth/logout",
            "/auth/refresh",
            "/auth/verify-email/resend",
            "/auth/verify-email/confirm",
            "/auth/forgot-password",
            "/auth/reset-password"
    };

    public static final String[] PUBLIC_READONLY_ENDPOINTS = {
            "/api/v1/amazon/images/**",
            "/api/v1/issues/categories/**",
            "/api/v1/comments/**",
            "/api/v1/issues/**",
            "/api/v1/votes/**",
            "/api/v1/locations/**",
            "/api/v1/users/**"
    };
}
