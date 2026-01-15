/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : CustomAuthenticationProvider
 * Author  : Vikas Kumar
 * Created : 15-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.security;

import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Duration;

//    authentication provider configuration links UserDetailService and PasswordEncoder
@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    private final UserRepository userRepository;

    public CustomAuthenticationProvider(UserDetailsService userDetailsService,
                                        PasswordEncoder passwordEncoder,
                                        UserRepository userRepository) {
        this.userRepository = userRepository;
        setUserDetailsService(userDetailsService);
        setPasswordEncoder(passwordEncoder);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {

        try {
            super.additionalAuthenticationChecks(userDetails, authentication);
        } catch (BadCredentialsException ex) {

            MuddaUser user = (MuddaUser) userDetails;
            user.recordFailedLoginAttempt(5, Duration.ofMinutes(15));
            userRepository.save(user);

            throw ex;
        }
    }
}
