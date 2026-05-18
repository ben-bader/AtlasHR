package com.hrms.attendance_service.verification;

import com.hrms.attendance_service.application.dto.AttendanceVerificationRequestDTO;
import com.hrms.attendance_service.application.service.QrTokenService;
import com.hrms.attendance_service.common.enums.VerificationMethod;
import com.hrms.common.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QrVerificationStrategy
        implements VerificationStrategy {

    private final QrTokenService qrTokenService;

    @Override
    public VerificationMethod getMethod() {

        return VerificationMethod.QR_CODE;
    }

    @Override
    public void verify(
            AttendanceVerificationRequestDTO request
    ) {

        if (request.getQrCode() == null || request.getQrCode().isBlank()) {

            throw new BadRequestException(
                    "QR code is required"
            );
        }

        boolean valid = qrTokenService.validateQr( request.getQrCode() );

        if (!valid) {

            throw new BadRequestException(
                    "QR code expired or invalid"
            );
        }
    }
}
