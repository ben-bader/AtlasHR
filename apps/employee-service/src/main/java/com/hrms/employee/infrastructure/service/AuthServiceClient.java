package com.hrms.employee.infrastructure.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Auth Service Client
 * 
 * Handles communication with Auth Service to create auth users
 * when employees are onboarded by the admin.
 */
@Service
@Slf4j
public class AuthServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Auth Service base URL from environment or default
     */
    @Value("${auth.service.url:http://auth-service:8081}")
    private String authServiceUrl;

    /**
     * Create user in Auth Service for newly onboarded employee
     * 
     * @param employeeId the employee ID
     * @param email the employee email
     * @param password the initial password
     * @param firstName the employee first name
     * @param lastName the employee last name
     * @return true if user creation was successful, false otherwise
     */
    public boolean createAuthUser(String employeeId, String email, String password,
                                  String firstName, String lastName) {
        try {
            log.info("Creating auth user for employeeId: {}", employeeId);

            // Prepare request payload
            CreateAuthUserRequest request = CreateAuthUserRequest.builder()
                    .employeeId(employeeId)
                    .password(password)
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .build();

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HTTP entity
            HttpEntity<CreateAuthUserRequest> entity = new HttpEntity<>(request, headers);

            // Call Auth Service
            String url = authServiceUrl + "/api/auth/admin/create-user";
            ResponseEntity<CreateAuthUserResponse> response = restTemplate.postForEntity(
                    url,
                    entity,
                    CreateAuthUserResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Auth user created successfully for employeeId: {}", employeeId);
                return true;
            } else {
                log.warn("Auth Service returned unsuccessful status: {} for employeeId: {}",
                        response.getStatusCode(), employeeId);
                return false;
            }

        } catch (RestClientException e) {
            log.error("Error calling Auth Service for employeeId {}: {}", employeeId, e.getMessage(), e);
            // In production, you might want to throw an exception or handle retries
            // For now, we log and return false to allow employee creation even if auth fails
            // You should monitor this and manually create auth users if needed
            return false;
        } catch (Exception e) {
            log.error("Unexpected error creating auth user for employeeId {}: {}", 
                    employeeId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Request DTO for creating auth user
     */
    public static class CreateAuthUserRequest {
        private String employeeId;
        private String password;
        private String email;
        private String firstName;
        private String lastName;

        public CreateAuthUserRequest() {}

        public CreateAuthUserRequest(String employeeId, String password, String email,
                                    String firstName, String lastName) {
            this.employeeId = employeeId;
            this.password = password;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        // Builder
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String employeeId;
            private String password;
            private String email;
            private String firstName;
            private String lastName;

            public Builder employeeId(String employeeId) {
                this.employeeId = employeeId;
                return this;
            }

            public Builder password(String password) {
                this.password = password;
                return this;
            }

            public Builder email(String email) {
                this.email = email;
                return this;
            }

            public Builder firstName(String firstName) {
                this.firstName = firstName;
                return this;
            }

            public Builder lastName(String lastName) {
                this.lastName = lastName;
                return this;
            }

            public CreateAuthUserRequest build() {
                return new CreateAuthUserRequest(employeeId, password, email, firstName, lastName);
            }
        }

        // Getters and Setters
        public String getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(String employeeId) {
            this.employeeId = employeeId;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }

    /**
     * Response DTO from Auth Service
     */
    public static class CreateAuthUserResponse {
        private String userId;
        private String username;
        private String message;

        public CreateAuthUserResponse() {}

        // Getters and Setters
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
