package com.hrms.attendance_service.verification;

import com.hrms.attendance_service.application.dto.AttendanceVerificationRequestDTO;
import com.hrms.attendance_service.common.enums.VerificationMethod;
import com.hrms.common.exception.BadRequestException;
import com.hrms.attendance_service.domain.model.EmployeeVerificationCredential;
import com.hrms.attendance_service.domain.repository.CredentialRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NfcVerificationStrategy
        implements VerificationStrategy {

    private final CredentialRepository credentialRepository;

    @Override
    public VerificationMethod getMethod() {

        return VerificationMethod.NFC;
    }

    @Override
    public void verify(
            AttendanceVerificationRequestDTO request
    ) {

        if (request.getNfcTag() == null
                || request.getNfcTag().isBlank()) {

            throw new BadRequestException(
                    "NFC tag is required"
            );
        }

        EmployeeVerificationCredential credential =
                credentialRepository
                        .findByEmployeeId(
                                request.getEmployeeId()
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Employee credential not found"
                                ));

        if (credential.getNfcTag() == null
                || !credential.getNfcTag()
                .equals(request.getNfcTag())) {

            throw new BadRequestException(
                    "Invalid NFC tag"
            );
        }
    }
}
