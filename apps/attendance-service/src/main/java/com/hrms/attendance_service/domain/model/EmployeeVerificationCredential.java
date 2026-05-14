package com.hrms.attendance_service.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employee_verification_credentials")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeVerificationCredential extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String employeeId;

    // QR
    @Column(unique = true)
    private String qrToken;

    private LocalDate qrExpiryDate;

    // NFC
    @Column(unique = true)
    private String nfcTag;

    // FACE
    private String faceId;

    // FINGERPRINT
    private String fingerprintId;

    @Builder.Default
    private Boolean active = true;
}
