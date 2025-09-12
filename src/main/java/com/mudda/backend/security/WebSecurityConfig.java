package com.mudda.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/**")
                .permitAll()
                .anyRequest()
                .authenticated())
            .csrf(AbstractHttpConfigurer::disable);
                // TODO: setup proper security chain
                // .formLogin((form) -> form
                // .loginPage("/login")
                // .permitAll())
                // .logout((logout) -> logout.permitAll())

        return http.build();
    }
}