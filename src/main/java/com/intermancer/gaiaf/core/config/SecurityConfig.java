package com.intermancer.gaiaf.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the Gaia-F REST API.
 *
 * <p>This application is a stateless REST API served behind an Nginx reverse proxy.
 * All browser requests reach the backend via the proxy, not directly from the browser,
 * so standard browser-based CSRF attacks are not applicable. CSRF protection is
 * therefore disabled. Sessions are also disabled in favour of stateless request handling.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain.
     *
     * @param http the {@link HttpSecurity} to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Stateless REST API — no session required
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // CSRF not applicable: requests arrive via Nginx reverse proxy, not directly from browsers
            .csrf(csrf -> csrf.disable())
            // All endpoints are permitted; add authentication rules here when needed
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
