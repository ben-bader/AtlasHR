package com.hrms.employee.domain.model;

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
public class ContactInfo {

    private String primaryPhone;
    private String alternatePhone;
    private String currentAddress;
    private String city;
    private String state;
    private String postalCode;

}
