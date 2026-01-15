/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : RateLimitFilter
 * Author  : Vikas Kumar
 * Created : 15-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.security;

import com.mudda.backend.AppProperties;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private final AppProperties appProperties;

    public RateLimitFilter(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();

        if (!shouldRateLimit(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        Bucket bucket = buckets.computeIfAbsent(ip, this::newBucket);

        if (bucket.tryConsume(1))
            filterChain.doFilter(request, response);
        else {
            response.setStatus(429);
            response.getWriter().println("Too many requests");
        }
    }

    private Bucket newBucket(String key) {
        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(appProperties.getRateLimit().getAuthCapacity())
                        .refillGreedy(
                                appProperties.getRateLimit().getAuthRefillTokens(),
                                Duration.ofMinutes(appProperties.getRateLimit().getAuthRefillMinutes())
                        )
                ).build();
    }

    private boolean shouldRateLimit(String path) {
        return path.startsWith("/auth");
    }
}
