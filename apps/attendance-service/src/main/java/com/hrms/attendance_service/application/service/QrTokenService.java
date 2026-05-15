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

    // Generate daily QR token
    public String generateQr(String employeeId) {

        EmployeeVerificationCredential credential =
                repository.findByEmployeeId(employeeId)
                        .orElse(
                                EmployeeVerificationCredential.builder()
                                        .employeeId(employeeId)
                                        .build()
                        );

        credential.setQrToken(UUID.randomUUID().toString());

        credential.setQrExpiryDate(LocalDate.now().plusDays(1));

        repository.save(credential);

        return credential.getQrToken();
    }

    // Validate QR token
    public boolean validateQr(String token) {

        return repository
                .findByQrTokenAndQrExpiryDateGreaterThanEqual(
                        token,
                        LocalDate.now()
                )
                .isPresent();
    }
}
