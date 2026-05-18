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
import com.hrms.auth.application.dto.CreateAuthUserRequest;
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

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    public AuthService(
            ObjectProvider<AuthenticationManager> authenticationManagerProvider,
            ObjectProvider<PasswordEncoder> passwordEncoderProvider) {
        this.authenticationManagerProvider = authenticationManagerProvider;
        this.passwordEncoderProvider = passwordEncoderProvider;
    }

 public AuthResponse register(RegisterRequest request) {
    try {
        // 1️⃣ Check duplicates
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

        // 2️⃣ Get encoder
        PasswordEncoder encoder = passwordEncoderProvider.getIfAvailable();
        if (encoder == null) {
            throw new RuntimeException("PasswordEncoder bean not found");
        }

        // 3️⃣ Get or create default role
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name("USER")
                                .description("Default user role")
                                .build()
                ));

        // 4️⃣ Build user (NOT saved yet)
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

        // 5️⃣ SAVE USER FIRST  ✅
        User savedUser = userRepository.save(user);

        // 6️⃣ Publish event AFTER save (ID exists now) ✅
        String roles = savedUser.getRoles()
                .stream()
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

        eventPublisher.publishUserCreated(event);

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
            
            // Support login with both employeeId and username
            String loginIdentifier = request.getUsername();
            
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginIdentifier,
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = tokenProvider.generateToken(userDetails);
            String refreshToken = tokenProvider.generateRefreshToken(userDetails);

            User user = userRepository.findByUsername(loginIdentifier)
                    .or(() -> userRepository.findByEmployeeId(loginIdentifier))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            
            log.info("User logged in successfully: {} (employeeId: {})", user.getUsername(), user.getEmployeeId());

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
                .or(() -> userRepository.findByEmployeeId(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        log.debug("User loaded from database: {}", username);
        return user;
    }

    /**
     * Create auth user for employee (Admin-driven)
     * Called by Employee Service when onboarding employees
     * 
     * @param request CreateAuthUserRequest with employeeId and password
     * @return AuthResponse with user details
     */
    public AuthResponse createAuthUser(CreateAuthUserRequest request) {
        try {
            log.info("Creating auth user for employeeId: {}", request.getEmployeeId());

            // Check if user already exists
            if (userRepository.existsByEmployeeId(request.getEmployeeId())) {
                log.warn("Auth user already exists for employeeId: {}", request.getEmployeeId());
                return AuthResponse.builder()
                        .message("User already exists for this employee")
                        .build();
            }

            // Get password encoder
            PasswordEncoder encoder = passwordEncoderProvider.getIfAvailable();
            if (encoder == null) {
                throw new RuntimeException("PasswordEncoder bean not found");
            }

            // Get or create default role
            Role userRole = roleRepository.findByName("USER")
                    .orElseGet(() -> roleRepository.save(
                            Role.builder()
                                    .name("USER")
                                    .description("Default user role")
                                    .build()
                    ));

            // Build user with employeeId
            User user = User.builder()
                    .employeeId(request.getEmployeeId())
                    .username(request.getEmployeeId()) // Use employeeId as username for login
                    .email(request.getEmail())
                    .password(encoder.encode(request.getPassword()))
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .roles(new HashSet<>(Set.of(userRole)))
                    .build();

            // Save user
            User savedUser = userRepository.save(user);

            // Publish event
            String roles = savedUser.getRoles()
                    .stream()
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

            eventPublisher.publishUserCreated(event);

            log.info("Auth user created successfully for employeeId: {}", request.getEmployeeId());

            return AuthResponse.builder()
                    .userId(savedUser.getId())
                    .username(savedUser.getUsername())
                    .message("User created successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error creating auth user: {}", e.getMessage(), e);
            return AuthResponse.builder()
                    .message("Failed to create user: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Logout user and blacklist token
     * 
     * @param token the JWT token to blacklist
     * @return success response
     */
    public AuthResponse logout(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return AuthResponse.builder()
                        .message("Invalid token")
                        .build();
            }

            // Validate token and extract claims
            if (!tokenProvider.validateToken(token)) {
                return AuthResponse.builder()
                        .message("Invalid or expired token")
                        .build();
            }

            // Extract token information
            String username = tokenProvider.getUsernameFromToken(token);
            io.jsonwebtoken.Claims claims = tokenProvider.parseToken(token);
            
            String tokenJti = claims.getId(); // JWT ID claim
            if (tokenJti == null || tokenJti.isEmpty()) {
                // If no jti claim, generate one from username + timestamp
                tokenJti = username + "_" + System.currentTimeMillis();
            }

            // Get user to extract userId and employeeId
            User user = userRepository.findByUsername(username)
                    .or(() -> userRepository.findByEmployeeId(username))
                    .orElse(null);

            if (user == null) {
                log.warn("User not found during logout: {}", username);
                return AuthResponse.builder()
                        .message("User not found")
                        .build();
            }

            // Get token expiration time
            java.util.Date expirationDate = claims.getExpiration();
            LocalDateTime expirationTime = expirationDate != null 
                    ? expirationDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
                    : LocalDateTime.now().plusHours(24);

            // Blacklist the token
            tokenBlacklistService.blacklistToken(
                    tokenJti,
                    user.getId(),
                    user.getEmployeeId(),
                    expirationTime,
                    "logout"
            );

            log.info("User logged out successfully: {} (employeeId: {})", username, user.getEmployeeId());

            return AuthResponse.builder()
                    .message("Logout successful")
                    .build();

        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage(), e);
            return AuthResponse.builder()
                    .message("Logout failed: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Get user by employeeId
     * 
     * @param employeeId the employee ID
     * @return UserDTO with user details
     */
    public UserDTO getUserByEmployeeId(String employeeId) {
        return userRepository.findByEmployeeId(employeeId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for employeeId: " + employeeId));
    }
}
