package com.mudda.backend.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    public WebSecurityConfig(UserDetailsService userDetailsService,
                             JwtAuthFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        disableCsrf(http);  // CSRF not needed in jwt
        configureAuthorization(http);   // add public and protected endpoints
        configureExceptionHandling(http);   // Handles authentication and authorization error
        configureSession(http); // Makes session stateless
        configureAuthentication(http);  // Set userDetails, authenticationProvider, JwtFilter, httpBasic

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
                        "/auth/register",
                        "/auth/login",
                        "/auth/refresh",
                        "/auth/logout", // TODO: should logout be authenticated ?
                        "/auth/verify-email"
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

    private void configureSession(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }

    private void configureAuthentication(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService)
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());
    }

    //    authentication provider configuration links UserDetailService and PasswordEncoder
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    //    Password encoder bean critical for secure password storage
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //    Get the spring authentication manager (Should never create our own)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}