package com.hrms.auth;

import com.hrms.auth.application.dto.LoginRequest;
import com.hrms.auth.application.dto.RegisterRequest;
import com.hrms.auth.application.service.AuthService;
import com.hrms.auth.domain.repository.RoleRepository;
import com.hrms.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthServiceApplicationTests {

	@Autowired
	private AuthService authService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
	}

	@Test
	void contextLoads() {
		assertNotNull(authService);
		assertNotNull(userRepository);
	}

	@Test
	void testUserRegistration() {
		RegisterRequest request = RegisterRequest.builder()
				.username("testuser")
				.email("testuser@example.com")
				.password("password123")
				.build();

		var response = authService.register(request);
		assertNotNull(response);
		assertEquals("User registered successfully", response.getMessage());
		assertTrue(userRepository.existsByUsername("testuser"));
	}

	@Test
	void testUserLogin() {
		// Register user first
		RegisterRequest registerRequest = RegisterRequest.builder()
				.username("testuser")
				.email("testuser@example.com")
				.password("password123")
				.build();
		authService.register(registerRequest);

		// Login
		LoginRequest loginRequest = LoginRequest.builder()
				.username("testuser")
				.password("password123")
				.build();

		var response = authService.login(loginRequest);
		assertNotNull(response);
		assertNotNull(response.getToken());
		assertEquals("testuser", response.getUsername());
	}

	@Test
	void testInvalidLogin() {
		LoginRequest loginRequest = LoginRequest.builder()
				.username("nonexistent")
				.password("password123")
				.build();

		var response = authService.login(loginRequest);
		assertNotNull(response);
		assertNull(response.getToken());
		assertEquals("Invalid username or password", response.getMessage());
	}

	@Test
	void testDuplicateUsername() {
		RegisterRequest request1 = RegisterRequest.builder()
				.username("testuser")
				.email("test1@example.com")
				.password("password123")
				.build();

		RegisterRequest request2 = RegisterRequest.builder()
				.username("testuser")
				.email("test2@example.com")
				.password("password123")
				.build();

		authService.register(request1);
		var response = authService.register(request2);
		assertEquals("Username already exists", response.getMessage());
	}

}
