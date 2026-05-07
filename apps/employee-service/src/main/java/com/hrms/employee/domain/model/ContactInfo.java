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

    private String primaryPhone;  // Moroccan format: +212XXXXXXXXX or 06/07XXXXXXXX
    private String alternatePhone;
    private String currentAddress; // Street name and number
    private String city; // Moroccan city
    private String province; // Moroccan province
    private String codePostal; // Moroccan postal code

}
