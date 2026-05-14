package com.hrms.attendance_service.application.service;

import com.hrms.attendance_service.domain.model.EmployeeVerificationCredential;
import com.hrms.attendance_service.domain.repository.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QrTokenService {

    private final CredentialRepository repository;

    // generate daily QR
    public String generateQr(String employeeId) {

        EmployeeVerificationCredential cred =
                repository.findByEmployeeId(employeeId)
                        .orElse(new EmployeeVerificationCredential());

        cred.setEmployeeId(employeeId);

        cred.setQrToken(UUID.randomUUID().toString());
        cred.setQrExpiryDate(LocalDate.now().plusDays(1));

        repository.save(cred);

        return cred.getQrToken();
    }

    // validate QR
    public boolean validateQr(String token) {

        return repository.findAll().stream()
                .anyMatch(c ->
                        token.equals(c.getQrToken())
                        && c.getQrExpiryDate() != null
                        && !c.getQrExpiryDate().isBefore(LocalDate.now())
                );
    }
}
