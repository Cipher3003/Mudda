package com.mudda.backend.security;

import com.mudda.backend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : UserIdAuthenticationSuccessHandler
 * Author  : Vikas Kumar
 * Created : 13-11-2025
 * ---------------------------------------------------------------
 */
@Component
public class UserIdAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public UserIdAuthenticationSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws ServletException, IOException {

        String userName = authentication.getName();
        Long userId = userRepository.findByUsername(userName)
                .orElseThrow(() -> new EntityNotFoundException("User with username: %s not found"
                        .formatted(userName)))
                .getUserId();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userId,
                authentication.getCredentials(),
                authentication.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        super.onAuthenticationSuccess(request, response, auth);
    }
}
