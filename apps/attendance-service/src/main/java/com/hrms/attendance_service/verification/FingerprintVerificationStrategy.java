package com.hrms.attendance_service.verification;

import com.hrms.attendance_service.application.dto.AttendanceVerificationRequestDTO;
import com.hrms.attendance_service.common.enums.VerificationMethod;
import com.hrms.common.exception.BadRequestException;

import org.springframework.stereotype.Component;

@Component
public class FingerprintVerificationStrategy
        implements VerificationStrategy {

    @Override
    public VerificationMethod getMethod() {

        return VerificationMethod.FINGERPRINT;
    }

    @Override
    public void verify(
            AttendanceVerificationRequestDTO request
    ) {

        Boolean matched =
                request.getFingerprintMatched();

        if (matched == null || !matched) {

            throw new BadRequestException(
                    "Fingerprint verification failed"
            );
        }
    }
}
