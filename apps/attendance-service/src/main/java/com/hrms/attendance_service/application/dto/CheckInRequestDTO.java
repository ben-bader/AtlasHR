package com.hrms.attendance_service.application.dto;

import com.hrms.attendance_service.common.enums.VerificationMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckInRequestDTO {

    @NotBlank
    private String employeeId;

    @NotNull
    private VerificationMethod method;

    @NotNull
    private VerificationPayloadDTO verificationPayload;
}
