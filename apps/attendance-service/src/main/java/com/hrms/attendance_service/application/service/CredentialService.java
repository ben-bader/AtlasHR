package com.hrms.attendance_service.application.service;

import com.hrms.attendance_service.domain.model.EmployeeVerificationCredential;
import com.hrms.attendance_service.domain.repository.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CredentialService {

    private final CredentialRepository repository;

    public EmployeeVerificationCredential getByEmployee(String employeeId) {
        return repository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Credentials not found"));
    }
}
