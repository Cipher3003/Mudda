package com.mudda.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    // TODO: upgrade to JWT

    private final CustomUserDetailsService userDetailsService;

    public WebSecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        (requests) -> requests
                                // Public Reads
                                .requestMatchers(
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/v3/api-docs")
                                .permitAll()
                                .requestMatchers("/media_url.html")
                                .permitAll()
                                .requestMatchers("/api/v1/votes/**")
                                .permitAll()
                                .requestMatchers("/api/v1/users/**")
                                .permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/amazon/images/**")
                                .permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/issues/categories/**")
                                .permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/comments/**")
                                .permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/issues/**")
                                .permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/locations/**")
                                .permitAll()
                                // Everything Else Needs Login
                                .anyRequest()
                                .authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, e) -> {
                            System.err.println("AUTH ERROR -> " + e.getMessage());
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
                        })
                        .accessDeniedHandler((request, response, e) -> {
                            System.err.println("ACCESS DENIED -> " + e.getMessage());
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
                        })
                )
                .userDetailsService(userDetailsService)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}