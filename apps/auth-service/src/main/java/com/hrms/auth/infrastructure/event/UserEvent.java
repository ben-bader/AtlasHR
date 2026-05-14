package com.hrms.auth.infrastructure.event;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {

    @JsonProperty("eventType")
    private String eventType;  // "user.created" or "user.deleted"

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("roles")
    private String roles;  // comma-separated
}
