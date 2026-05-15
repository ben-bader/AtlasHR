package com.hrms.attendance_service.domain.model;

import java.time.LocalDateTime;

import com.hrms.attendance_service.common.enums.VerificationMethod;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "devices")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String deviceUid;

    @Column(nullable = false)
    private String deviceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationMethod deviceType;

    @Column(unique = true, nullable = false)
    private String apiKey;

    private String location;

    @Column(nullable = false)
    private Boolean active;

    private LocalDateTime lastSeen;

    @PrePersist
    public void prePersist() {

        if (active == null) {
            active = true;
        }
    }
}
