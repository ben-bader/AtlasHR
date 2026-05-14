package com.hrms.attendance_service.application.service;

import com.hrms.attendance_service.application.dto.AttendanceVerificationRequestDTO;
import com.hrms.attendance_service.common.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationEngineService {

    private final QrTokenService qrTokenService;

    public void verify(AttendanceVerificationRequestDTO request) {

        switch (request.getMethod()) {

            case QR_CODE -> verifyQr(request);
            case NFC -> verifyNfc(request);
            case FACE_RECOGNITION -> verifyFace(request);
            case FINGERPRINT -> verifyFingerprint(request);

            default -> throw new BadRequestException("Unsupported method");
        }
    }

    private void verifyQr(AttendanceVerificationRequestDTO request) {

        if (request.getQrCode() == null)
            throw new BadRequestException("QR required");

        if (!qrTokenService.validateQr(request.getQrCode()))
            throw new BadRequestException("QR expired/invalid");
    }

    private void verifyNfc(AttendanceVerificationRequestDTO request) {

        if (request.getNfcTag() == null)
            throw new BadRequestException("NFC required");
    }

    private void verifyFace(AttendanceVerificationRequestDTO request) {

        if (request.getFaceMatchScore() == null ||
                request.getFaceMatchScore() < 0.85) {

            throw new BadRequestException("Face failed");
        }
    }

    private void verifyFingerprint(AttendanceVerificationRequestDTO request) {

        if (request.getFingerprintMatched() == null ||
                !request.getFingerprintMatched()) {

            throw new BadRequestException("Fingerprint failed");
        }
    }
}
