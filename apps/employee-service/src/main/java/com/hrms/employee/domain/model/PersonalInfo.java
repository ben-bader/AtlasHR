package com.hrms.employee.domain.model;

import java.time.LocalDate;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalInfo {

    private LocalDate dateOfBirth;
    private String gender;
    private String nationality;
    private String bloodGroup;
    private String maritalStatus;

}
