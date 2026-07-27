package com.mudda.backend.security;

import com.mudda.backend.AppProperties;
import com.mudda.backend.exceptions.ApiError;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static com.mudda.backend.security.SecurityUrlPatterns.*;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthFilter jwtAuthFilter;
    private final AppProperties appProperties;
    private final PersistentTokenRepository persistentTokenRepository;
    private final ObjectMapper objectMapper;

    public WebSecurityConfig(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            JwtAuthFilter jwtAuthFilter,
            AppProperties appProperties,
            PersistentTokenRepository persistentTokenRepository,
            ObjectMapper objectMapper
    ) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthFilter = jwtAuthFilter;
        this.appProperties = appProperties;
        this.persistentTokenRepository = persistentTokenRepository;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {

        enableCors(http);   // enable CORS for web
        configureCsrf(http);  // CSRF not needed in jwt
        configureAuthorization(http);   // add public and protected endpoints
        configureExceptionHandling(http);   // Handles authentication and authorization error
        configureSession(http); // Makes sessions as needed for web
        configureRememberMe(http);   // Add remember me cookie
        configureForms(http);   // Add login and logout forms
        configureAuthentication(http);  // Set userDetails, authenticationProvider, JwtFilter, httpBasic

        return http.build();
    }

    private void enableCors(HttpSecurity http) {
        http.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer
                .configurationSource(corsConfigurationSource()));
    }

    private void configureCsrf(HttpSecurity http) {
        CsrfTokenRequestAttributeHandler csrfHandler = new CsrfTokenRequestAttributeHandler();
        csrfHandler.setCsrfRequestAttributeName(null);  // forces spring to generate csrf for all request

        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(csrfHandler) // TODO: transition to handshake endpoint to send csrf token
                .requireCsrfProtectionMatcher(request -> {
//                    Ignore if request is safe (non-state changing)
                    if (List.of("GET", "HEAD", "TRACE", "OPTIONS").contains(request.getMethod())) return false;

//                    Ignore if request from mobile
                    String clientType = request.getHeader("X-Client-Type");
                    if (clientType != null && List.of("mobile", "mobile-android", "mobile-ios").contains(clientType))
                        return false;

//                    Ignore if request has bearer token (jwt)
                    String authorization = request.getHeader("Authorization");
                    return authorization == null || !authorization.startsWith("Bearer ");
//                    Force everything else to from browser with csrf
                }));
    }

    private void configureAuthorization(HttpSecurity http) {
        http.authorizeHttpRequests(auth -> auth

                // ===== PUBLIC ENDPOINTS =====
                .requestMatchers(SEED).permitAll()
                .requestMatchers(SWAGGER).permitAll()
                .requestMatchers(PUBLIC_PAGES).permitAll()
                .requestMatchers(AUTH_PUBLIC).permitAll()
                .requestMatchers(HttpMethod.GET, PUBLIC_READONLY_API).permitAll()

                // ===== PROTECTED ENDPOINTS =====
                .anyRequest().authenticated());
    }

    // TODO: handle auth error for mobile only endpoints and redirect to login for web
    private void configureExceptionHandling(HttpSecurity http) {
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

    //    TODO: configure session side effects for resetting login failure and count
    private void configureSession(HttpSecurity http) {
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
    }

    private void configureRememberMe(HttpSecurity http) {
        http.rememberMe(rememberMe -> rememberMe
                        .key(appProperties.getSession().getKey())
                        .tokenRepository(persistentTokenRepository)
                        .userDetailsService(userDetailsService)
//              .tokenValiditySeconds()   // Set if not happy with default (2 weeks)
//              .rememberMeParameter()    // set if not happy with default (remember-me)
        );
    }

    //    TODO: handle spring success and failure event to record login attempts
    private void configureForms(HttpSecurity http) {
        http.formLogin(formLogin -> formLogin
                .loginProcessingUrl("/login")
                .successHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json;charset=utf-8");

                    Map<String, Object> map = Map.of(
                            "status", "SUCCESS",
                            "username", authentication.getName(),
                            "timestamp", System.currentTimeMillis()
                    );
                    response.getWriter().write(objectMapper.writeValueAsString(map));
                })
                .failureHandler((request, response, exception) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=utf-8");

                    ApiError apiError = new ApiError(HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED",
                            exception.getMessage(), null);
                    response.getWriter().write(objectMapper.writeValueAsString(apiError));
                })
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/auth/web/logout")  // TODO: maybe change logout and login web url to default
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json;charset=utf-8");

                    Map<String, Object> map = Map.of(
                            "status", "SUCCESS",
                            "message", "Logout successful"
                    );
                    response.getWriter().write(objectMapper.writeValueAsString(map));
                })
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .permitAll()
        );
    }

    private void configureAuthentication(HttpSecurity http) {
        http
                .userDetailsService(userDetailsService)
                .authenticationProvider(getAuthenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    }

    private AuthenticationProvider getAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

    //    Get the spring authentication manager (Should never create our own)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(appProperties.getCors().getAllowedOrigins().split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "X-XSRF-TOKEN", "X-Client-Type", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}