package com.hrms.attendance_service.verification;

import com.hrms.attendance_service.application.dto.AttendanceVerificationRequestDTO;
import com.hrms.attendance_service.common.enums.VerificationMethod;

public interface VerificationStrategy {

    VerificationMethod getMethod();

    void verify(AttendanceVerificationRequestDTO request);
}
