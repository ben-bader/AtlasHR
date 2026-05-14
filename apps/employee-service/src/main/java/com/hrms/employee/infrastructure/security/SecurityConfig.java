// FIX: Employee service had NO SecurityConfig → Spring Boot auto-configured
// form login → caused 302 redirect to /login instead of 401.
// This config disables form login, enforces stateless sessions, and registers
// TrustHeadersFilter INSIDE the security chain before auth checks run.

package com.hrms.employee.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final TrustHeadersFilter trustHeadersFilter = new TrustHeadersFilter();

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // STATELESS - no sessions, no redirects, no cookies
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Disable CSRF - stateless REST API
            .csrf(csrf -> csrf.disable())

            // CRITICAL: Disable form login - must return 401 not 302
            .formLogin(form -> form.disable())

            // CRITICAL: Disable HTTP Basic - must return 401 not 302
            .httpBasic(basic -> basic.disable())

            // CRITICAL: Register TrustHeadersFilter BEFORE Spring Security
            // evaluates the request. Without this, Security rejects the
            // request before the filter can populate SecurityContext.
            .addFilterBefore(trustHeadersFilter,
                UsernamePasswordAuthenticationFilter.class)

            .authorizeHttpRequests(auth -> auth
                // Health checks - no auth needed
                .requestMatchers("/actuator/health/**").permitAll()
                .requestMatchers("/actuator/info").permitAll()
                // All other requests must be authenticated
                .anyRequest().authenticated());

        log.info("Employee Service SecurityConfig loaded - form login disabled, TrustHeadersFilter active");
        return http.build();
    }
}