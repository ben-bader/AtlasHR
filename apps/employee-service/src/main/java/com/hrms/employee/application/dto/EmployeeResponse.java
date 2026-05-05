package com.hrms.employee.application.dto;

import com.hrms.employee.common.enums.EmploymentStatus;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {

    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String aadharNumber;
    private String panNumber;
    private LocalDate joiningDate;
    private LocalDate dateOfBirth;
    private String gender;
    private String nationality;
    private String bloodGroup;
    private EmploymentStatus status;
    private String departmentName;
    private Long departmentId;
    private String designationName;
    private String designationId;
    private String reportingManagerName;
    private String reportingManagerId;
    private String grade;
    private String primaryPhone;
    private String currentAddress;
    private String city;
    private String state;
    private String postalCode;
    private String bankName;
    private String accountNumber;
    private List<EmployeeInsuranceResponse> insurances;

}
