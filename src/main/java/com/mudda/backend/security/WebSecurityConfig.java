package com.mudda.backend.security;

import com.mudda.backend.AppProperties;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.List;

import static com.mudda.backend.security.SecurityEndpoints.*;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthFilter jwtAuthFilter;
    private final RateLimitFilter rateLimitFilter;
    private final AppProperties appProperties;
    private final PersistentTokenRepository persistentTokenRepository;

    public WebSecurityConfig(UserDetailsService userDetailsService,
                             PasswordEncoder passwordEncoder,
                             JwtAuthFilter jwtAuthFilter,
                             RateLimitFilter rateLimitFilter,
                             AppProperties appProperties,
                             PersistentTokenRepository persistentTokenRepository) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthFilter = jwtAuthFilter;
        this.rateLimitFilter = rateLimitFilter;
        this.appProperties = appProperties;
        this.persistentTokenRepository = persistentTokenRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        enableCors(http);   // enable CORS for web
        configureCsrf(http);  // CSRF not needed in jwt
        configureAuthorization(http);   // add public and protected endpoints
        configureExceptionHandling(http);   // Handles authentication and authorization error
        configureSession(http); // Makes sessions as needed for web
        configureRememberMe(http);   // Add remember me cookie
        configureForms(http);   // Add login and logout forms
        configureAuthentication(http);  // Set userDetails, authenticationProvider, JwtFilter, httpBasic
        configureRateLimit(http);   // Set rateLimitFilter before authentication

        return http.build();
    }

    private void enableCors(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
    }

    private void configureCsrf(HttpSecurity http) throws Exception {
        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer
                .ignoringRequestMatchers("/auth/mobile/**"));
    }

    private void configureAuthorization(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth

                // ===== PUBLIC ENDPOINTS =====
                .requestMatchers(SEED_ENDPOINTS).permitAll()
                .requestMatchers(SWAGGER_ENDPOINTS).permitAll()
                .requestMatchers(PUBLIC_STATIC_PAGES).permitAll()
                .requestMatchers(AUTH_MOBILE_ENDPOINTS).permitAll()
                .requestMatchers(AUTH_PUBLIC_ENDPOINTS).permitAll()
                .requestMatchers(HttpMethod.GET, PUBLIC_READONLY_ENDPOINTS).permitAll()

                // ===== PROTECTED ENDPOINTS =====
                .anyRequest().authenticated());
    }

    // TODO: handle auth error for mobile only endpoints and redirect to login for web
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

    //    TODO: configure session side effects for resetting login failure and count
    private void configureSession(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
    }

    private void configureRememberMe(HttpSecurity http) throws Exception {
        http.rememberMe(rememberMe -> rememberMe
                        .key(appProperties.getSession().getKey())
                        .tokenRepository(persistentTokenRepository)
                        .userDetailsService(userDetailsService)
//              .tokenValiditySeconds()   // Set if not happy with default (2 weeks)
//              .rememberMeParameter()    // set if not happy with default (remember-me)
        );
    }

    // TODO: handle session login, logout errors
    private void configureForms(HttpSecurity http) throws Exception {
        http.formLogin(formLogin -> formLogin
                .loginPage("/login.html"));

        http.logout(logout -> logout
                .logoutUrl("/auth/web/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
        );
    }

    private void configureAuthentication(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService)
                .authenticationProvider(getAuthenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    }

    private AuthenticationProvider getAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

    private void configureRateLimit(HttpSecurity http) {
        http.addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class);
    }

    //    Get the spring authentication manager (Should never create our own)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(appProperties.getCors().getAllowedOrigins().split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

}