package com.mudda.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : SecurityUtil
 * Author  : Vikas Kumar
 * Created : 13-11-2025
 * ---------------------------------------------------------------
 */
public class SecurityUtil {
    public static Long getUserIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser"))
            return null;

        return (Long) auth.getPrincipal();
    }
}
