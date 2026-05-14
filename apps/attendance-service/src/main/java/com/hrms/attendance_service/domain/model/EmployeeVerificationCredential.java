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

    private String employeeId;

    // QR (daily token)
    private String qrToken;
    private LocalDate qrExpiryDate;

    // NFC
    private String nfcTag;

    // FACE
    private String faceId;

    // FINGERPRINT
    private String fingerprintId;

    private Boolean active;
}
