package com.hrms.attendance_service.verification;

import com.hrms.attendance_service.application.dto.AttendanceVerificationRequestDTO;
import com.hrms.attendance_service.common.enums.VerificationMethod;
import com.hrms.attendance_service.common.exceptions.BadRequestException;

import org.springframework.stereotype.Component;

@Component
public class FaceVerificationStrategy
        implements VerificationStrategy {

    private static final double FACE_THRESHOLD = 0.85;

    @Override
    public VerificationMethod getMethod() {

        return VerificationMethod.FACE_RECOGNITION;
    }

    @Override
    public void verify(
            AttendanceVerificationRequestDTO request
    ) {

        Double score = request.getFaceMatchScore();

        if (score == null) {

            throw new BadRequestException(
                    "Face score is required"
            );
        }

        if (score < FACE_THRESHOLD) {

            throw new BadRequestException(
                    "Face verification failed"
            );
        }
    }
}
