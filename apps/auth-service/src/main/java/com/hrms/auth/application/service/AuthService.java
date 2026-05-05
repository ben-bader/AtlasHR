package com.hrms.auth.application.service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hrms.auth.application.dto.AuthResponse;
import com.hrms.auth.application.dto.LoginRequest;
import com.hrms.auth.application.dto.RegisterRequest;
import com.hrms.auth.application.dto.UserDTO;
import com.hrms.auth.domain.model.Role;
import com.hrms.auth.domain.model.User;
import com.hrms.auth.domain.repository.RoleRepository;
import com.hrms.auth.domain.repository.UserRepository;
import com.hrms.auth.infrastructure.event.UserEvent;
import com.hrms.auth.infrastructure.event.UserEventPublisher;
import com.hrms.auth.infrastructure.security.JwtTokenProvider;

import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final ObjectProvider<AuthenticationManager> authenticationManagerProvider;
    private final ObjectProvider<PasswordEncoder> passwordEncoderProvider;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserEventPublisher eventPublisher;

    public AuthService(
            ObjectProvider<AuthenticationManager> authenticationManagerProvider,
            ObjectProvider<PasswordEncoder> passwordEncoderProvider) {
        this.authenticationManagerProvider = authenticationManagerProvider;
        this.passwordEncoderProvider = passwordEncoderProvider;
    }

    public AuthResponse register(RegisterRequest request) {
        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                log.warn("Registration failed: Username {} already exists", request.getUsername());
                return AuthResponse.builder()
                        .message("Username already exists")
                        .build();
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("Registration failed: Email {} already exists", request.getEmail());
                return AuthResponse.builder()
                        .message("Email already exists")
                        .build();
            }

            PasswordEncoder encoder = passwordEncoderProvider.getIfAvailable();
            if (encoder == null) {
                throw new RuntimeException("PasswordEncoder not available");
            }

            Role userRole = roleRepository.findByName("USER")
                    .orElseGet(() -> {
                        Role role = Role.builder()
                                .name("USER")
                                .description("Default user role")
                                .build();
                        return roleRepository.save(role);
                    });

            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(encoder.encode(request.getPassword()))
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .roles(new HashSet<>(Set.of(userRole)))
                    .build();

            // Publish user.created event
            String roles = savedUser.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(","));
            UserEvent event = UserEvent.builder()
                    .eventType("user.created")
                    .userId(savedUser.getId().toString())
                    .username(savedUser.getUsername())
                    .email(savedUser.getEmail())
                    .timestamp(LocalDateTime.now())
                    .roles(roles)
                    .build();
            eventPublisher.publishUserCreatedEvent(event);

            User savedUser = userRepository.save(user);
            log.info("User registered successfully: {}", savedUser.getUsername());

            return AuthResponse.builder()
                    .userId(savedUser.getId())
                    .username(savedUser.getUsername())
                    .message("User registered successfully")
                    .build();
        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage(), e);
            return AuthResponse.builder()
                    .message("Registration failed: " + e.getMessage())
                    .build();
        }
    }

    public AuthResponse login(LoginRequest request) {
        try {
            AuthenticationManager authManager = authenticationManagerProvider.getIfAvailable();
            if (authManager == null) {
                throw new RuntimeException("AuthenticationManager not available");
            }
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = tokenProvider.generateToken(userDetails);
            String refreshToken = tokenProvider.generateRefreshToken(userDetails);

            User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
            log.info("User logged in successfully: {}", user.getUsername());

            return AuthResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .message("Login successful")
                    .build();
        } catch (AuthenticationException e) {
            log.warn("Login failed for user {}: {}", request.getUsername(), e.getMessage());
            return AuthResponse.builder()
                    .message("Invalid username or password")
                    .build();
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage(), e);
            return AuthResponse.builder()
                    .message("Login failed: " + e.getMessage())
                    .build();
        }
    }

    public AuthResponse refreshToken(String refreshToken) {
        try {
            if (tokenProvider.validateToken(refreshToken)) {
                String username = tokenProvider.getUsernameFromToken(refreshToken);
                UserDetails userDetails = loadUserByUsername(username);
                String newToken = tokenProvider.generateToken(userDetails);

                User user = userRepository.findByUsername(username).orElseThrow();
                log.info("Token refreshed for user: {}", username);

                return AuthResponse.builder()
                        .token(newToken)
                        .refreshToken(refreshToken)
                        .userId(user.getId())
                        .username(user.getUsername())
                        .message("Token refreshed successfully")
                        .build();
            } else {
                log.warn("Invalid or expired refresh token");
                return AuthResponse.builder()
                        .message("Invalid or expired refresh token")
                        .build();
            }
        } catch (Exception e) {
            log.error("Token refresh error: {}", e.getMessage(), e);
            return AuthResponse.builder()
                    .message("Token refresh failed: " + e.getMessage())
                    .build();
        }
    }

    public UserDTO getUserById(UUID userId) {
        return userRepository.findById(userId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToDTO)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .enabled(user.getEnabled())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        log.debug("User loaded from database: {}", username);
        return user;
    }
}
