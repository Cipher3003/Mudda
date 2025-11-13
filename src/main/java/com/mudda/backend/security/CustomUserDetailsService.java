package com.mudda.backend.security;

import com.mudda.backend.user.User;
import com.mudda.backend.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : CustomUserDetailsService
 * Author  : Vikas Kumar
 * Created : 13-11-2025
 * ---------------------------------------------------------------
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username).orElseThrow(
                () -> new UsernameNotFoundException("UserName: %s not found".formatted(username)));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserName())
                .password(user.getHashedPassword())
                .authorities("USER")
                .build();
    }
}
