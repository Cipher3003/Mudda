package com.mudda.backend.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.mudda.backend.security.SecurityEndpoints.*;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final JwtAuthFilter jwtAuthFilter;

    public WebSecurityConfig(UserDetailsService userDetailsService,
                             CustomAuthenticationProvider customAuthenticationProvider,
                             JwtAuthFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    //    TODO: add CORS
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

                // ===== PUBLIC ENDPOINTS =====
                .requestMatchers(SEED_ENDPOINTS).permitAll()
                .requestMatchers(SWAGGER_ENDPOINTS).permitAll()
                .requestMatchers(PUBLIC_STATIC_PAGES).permitAll()
                .requestMatchers(AUTH_PUBLIC_ENDPOINTS).permitAll()
                .requestMatchers(HttpMethod.GET, PUBLIC_READONLY_ENDPOINTS).permitAll()

                // ===== PROTECTED ENDPOINTS =====
                .anyRequest().authenticated());
    }

    private void configureExceptionHandling(HttpSecurity http) throws Exception {
        http.exceptionHandling(exception -> exception

//                Not logged in
                .authenticationEntryPoint((request, response, e) -> {
                    System.err.println("AUTH ERROR -> " + e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=utf-8");
                    response.getWriter().write("""
                            {
                            "error":"UNAUTHORIZED",
                            "message":"Authentication required or token expired"
                            }
                            """);
                })

//                Logged in but not allowed
                .accessDeniedHandler((request, response, e) -> {
                    System.err.println("ACCESS DENIED -> " + e.getMessage());
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
                    response.setContentType("application/json;charset=utf-8");
                    response.getWriter().write("""
                            {
                            "error":"FORBIDDEN",
                            "message":"You do not have permission to access this resource"
                            }
                            """);
                }));
    }

    private void configureSession(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }

    private void configureAuthentication(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService)
                .authenticationProvider(customAuthenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());
    }

    //    Get the spring authentication manager (Should never create our own)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}