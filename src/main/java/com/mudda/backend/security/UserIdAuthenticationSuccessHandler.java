/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : UserIdAuthenticationSuccessHandler
 * Author  : Vikas Kumar
 * Created : 13-11-2025
 * ---------------------------------------------------------------
 */
package com.mudda.backend.security;

import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
public class UserIdAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public UserIdAuthenticationSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws ServletException, IOException {

        MuddaUser user = (MuddaUser) authentication.getPrincipal();
        user.resetLoginFailures();
        userRepository.save(user);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
