package com.hrms.attendance_service.domain.repository;

import com.hrms.attendance_service.domain.model.EmployeeVerificationCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CredentialRepository extends JpaRepository<EmployeeVerificationCredential, Long> {

    Optional<EmployeeVerificationCredential> findByEmployeeId(String employeeId);

    Optional<EmployeeVerificationCredential> findByQrTokenAndQrExpiryDateGreaterThanEqual( String qrToken, LocalDate date);

    Optional<EmployeeVerificationCredential> findByNfcTag(String nfcTag);
}
