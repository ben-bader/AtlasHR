package com.hrms.attendance_service.application.dto;

import com.hrms.attendance_service.common.enums.VerificationMethod;

import lombok.Data;

@Data
public class AttendanceVerificationRequestDTO {

    private String employeeId;

    private VerificationMethod method;

    // QR
    private String qrCode;

    // NFC
    private String nfcTag;

    // FACE
    private Double faceMatchScore;

    // FINGERPRINT
    private Boolean fingerprintMatched;

    private String deviceUid;
}
