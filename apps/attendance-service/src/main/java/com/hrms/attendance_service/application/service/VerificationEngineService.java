package com.hrms.attendance_service.application.service;

import com.hrms.attendance_service.application.dto.AttendanceVerificationRequestDTO;
import com.hrms.attendance_service.verification.VerificationFactory;
import com.hrms.attendance_service.verification.VerificationStrategy;
import com.hrms.common.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationEngineService {

    private final VerificationFactory verificationFactory;

    public void verify(
            AttendanceVerificationRequestDTO request
    ) {

        validateRequest(request);

        VerificationStrategy strategy =
                verificationFactory.getStrategy(
                        request.getMethod()
                );

        strategy.verify(request);
    }

    private void validateRequest(
            AttendanceVerificationRequestDTO request
    ) {

        if (request == null) {

            throw new BadRequestException(
                    "Request body is required"
            );
        }

        if (request.getEmployeeId() == null
                || request.getEmployeeId().isBlank()) {

            throw new BadRequestException(
                    "Employee ID is required"
            );
        }

        if (request.getMethod() == null) {

            throw new BadRequestException(
                    "Verification method is required"
            );
        }
    }
}
