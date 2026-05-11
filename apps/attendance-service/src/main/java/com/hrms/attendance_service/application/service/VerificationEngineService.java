package com.hrms.attendance_service.application.service;

import com.hrms.attendance_service.application.dto.VerificationPayloadDTO;
import com.hrms.attendance_service.common.enums.VerificationMethod;
import com.hrms.attendance_service.common.exceptions.ValidationException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationEngineService {

    public void validate(VerificationMethod method,
                         VerificationPayloadDTO payload) {

        switch (method) {

            case QR_CODE -> validateQRCode(payload);

            case NFC -> validateNFC(payload);

            case FACE_RECOGNITION -> validateFace(payload);

            case FINGERPRINT -> validateFingerprint(payload);

            default -> throw new ValidationException(
                    "Unsupported verification method"
            );
        }
    }

    // ================= QR =================
    private void validateQRCode(VerificationPayloadDTO payload) {

        if (payload == null ||
                payload.getQrCode() == null ||
                payload.getQrCode().isBlank()) {

            throw new ValidationException(
                    "Invalid QR Code"
            );
        }
    }

    // ================= NFC =================
    private void validateNFC(VerificationPayloadDTO payload) {

        if (payload == null ||
                payload.getNfcTag() == null ||
                payload.getNfcTag().isBlank()) {

            throw new ValidationException(
                    "Invalid NFC Tag"
            );
        }
    }

    // ================= FACE =================
    private void validateFace(VerificationPayloadDTO payload) {

        if (payload == null ||
                payload.getFaceMatchScore() == null) {

            throw new ValidationException(
                    "Face verification failed"
            );
        }

        double threshold = 0.85;

        if (payload.getFaceMatchScore() < threshold) {

            throw new ValidationException(
                    "Face match score too low"
            );
        }
    }

    // ================= FINGERPRINT =================
    private void validateFingerprint(VerificationPayloadDTO payload) {

        if (payload == null ||
                payload.getFingerprintMatched() == null ||
                !payload.getFingerprintMatched()) {

            throw new ValidationException(
                    "Fingerprint verification failed"
            );
        }
    }
}
