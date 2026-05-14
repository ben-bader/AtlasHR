package com.hrms.auth.application.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AuthResponse {
    private String token;
    private String refreshToken;
    private UUID userId;
    private String username;
    private String message;

}
