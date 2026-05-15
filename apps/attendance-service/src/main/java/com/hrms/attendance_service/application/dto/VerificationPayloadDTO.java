package com.hrms.attendance_service.application.dto;

import lombok.Data;

@Data
public class VerificationPayloadDTO {

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