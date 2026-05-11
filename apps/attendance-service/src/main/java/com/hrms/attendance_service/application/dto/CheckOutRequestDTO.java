package com.hrms.attendance_service.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckOutRequestDTO {

    @NotBlank
    private String employeeId;
}
