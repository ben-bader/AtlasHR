package com.hrms.auth.presentation.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hrms.auth.application.dto.AuthResponse;
import com.hrms.auth.application.dto.CreateAuthUserRequest;
import com.hrms.auth.application.dto.LoginRequest;
import com.hrms.auth.application.dto.RegisterRequest;
import com.hrms.auth.application.dto.UserDTO;
import com.hrms.auth.application.service.AuthService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * DEPRECATED: Public registration endpoint
     * This endpoint is disabled as authentication is now admin-driven.
     * Use POST /api/auth/admin/create-user instead
     */
    @PostMapping("/register")
    @Deprecated(since = "2.0", forRemoval = true)
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        log.warn("Deprecated public registration endpoint called - use admin user creation instead");
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(AuthResponse.builder()
                        .message("Public registration is disabled. Please contact admin for user creation.")
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        log.info("Login request for user: {}", request.getUsername());
        AuthResponse response = authService.login(request);

        if (response.getToken() != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthResponse> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder().message("Invalid authorization header").build());
        }

        String token = authHeader.substring(7);
        AuthResponse response = authService.logout(token);

        if ("Logout successful".equals(response.getMessage())) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder().message("Invalid authorization header").build());
        }

        String refreshToken = authHeader.substring(7);
        AuthResponse response = authService.refreshToken(refreshToken);

        if (response.getToken() != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Admin endpoint to create user for employees
     * Called by Employee Service during employee onboarding
     */
    @PostMapping("/admin/create-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> createAuthUser(@RequestBody CreateAuthUserRequest request) {
        log.info("Admin request to create auth user for employeeId: {}", request.getEmployeeId());
        AuthResponse response = authService.createAuthUser(request);

        if ("User created successfully".equals(response.getMessage())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        log.info("Getting current user info: {}", authentication.getName());
        UserDTO user = authService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID userId) {
        log.info("Getting user info for userId: {}", userId);
        UserDTO user = authService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/user/employee/{employeeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserByEmployeeId(@PathVariable String employeeId) {
        log.info("Getting user info for employeeId: {}", employeeId);
        UserDTO user = authService.getUserByEmployeeId(employeeId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth Service is running");
    }
}
