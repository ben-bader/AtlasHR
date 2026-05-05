package com.hrms.employee.infrastructure.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
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
