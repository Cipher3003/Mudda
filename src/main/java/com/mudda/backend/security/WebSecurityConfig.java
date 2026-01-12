package com.mudda.backend.security;

import com.mudda.backend.user.UserService;
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

    private final UserService userService;

    public WebSecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        disableCsrf(http);
        configureAuthorization(http);
        configureExceptionHandling(http);
        configureAuthentication(http);

        return http.build();
    }

    private void disableCsrf(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
    }

    private void configureAuthorization(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                // Swagger / Docs
                .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/v3/api-docs"
                ).permitAll()

//                Public HTML pages
                .requestMatchers(
                        "/",
                        "/index.html",
                        "/home.html",
                        "/issue.html",
                        "/seed.html",
                        "/media_url.html",
                        "/login.html"
                ).permitAll()

//                Public API endpoints
                .requestMatchers(
                        "/api/v1/seed/**",
                        "/api/v1/votes/**",
                        "/api/v1/users/**",
                        "/login",
                        "/register"
                ).permitAll()

//                Public GET endpoints
                .requestMatchers(HttpMethod.GET,
                        "/api/v1/amazon/images/**",
                        "/api/v1/issues/categories/**",
                        "/api/v1/comments/**",
                        "/api/v1/issues/**",
                        "/api/v1/locations/**"
                ).permitAll()

                // Everything Else Needs Login
                .anyRequest().authenticated());
    }

    private void configureExceptionHandling(HttpSecurity http) throws Exception {
        http.exceptionHandling(exception -> exception

//                Not logged in
                .authenticationEntryPoint((request, response, e) -> {
                    System.err.println("AUTH ERROR -> " + e.getMessage());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
                })

//                Logged in but not allowed
                .accessDeniedHandler((request, response, e) -> {
                    System.err.println("ACCESS DENIED -> " + e.getMessage());
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
                }));
    }

    private void configureAuthentication(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userService)
                .formLogin(form -> form
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout.
                        logoutSuccessUrl("/login?logout"));
    }

}