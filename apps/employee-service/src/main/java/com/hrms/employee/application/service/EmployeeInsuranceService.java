package com.hrms.employee.application.service;

import com.hrms.employee.application.dto.AddInsuranceRequest;
import com.hrms.employee.application.dto.EmployeeInsuranceResponse;
import com.hrms.employee.common.constants.ApplicationConstants;
import com.hrms.employee.common.enums.InsuranceStatus;
import com.hrms.employee.common.enums.InsuranceType;
import com.hrms.employee.common.utils.IdGeneratorFactory;
import com.hrms.employee.domain.model.*;
import com.hrms.employee.domain.repository.EmployeeInsuranceRepository;
import com.hrms.employee.domain.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class EmployeeInsuranceService {

    private final EmployeeInsuranceRepository employeeInsuranceRepository;
    private final EmployeeRepository employeeRepository;
    private final IdGeneratorFactory idGeneratorFactory;

    public EmployeeInsuranceService(EmployeeInsuranceRepository employeeInsuranceRepository,
                                   EmployeeRepository employeeRepository,
                                   IdGeneratorFactory idGeneratorFactory) {
        this.employeeInsuranceRepository = employeeInsuranceRepository;
        this.employeeRepository = employeeRepository;
        this.idGeneratorFactory = idGeneratorFactory;
    }

    /**
     * Add a new insurance for an employee
     */
    public EmployeeInsuranceResponse addInsurance(AddInsuranceRequest request) {
        log.info("Adding insurance for employee: {}", request.getEmployeeId());

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.EMPLOYEE_NOT_FOUND));

        String uniquePolicyNumber = UUID.randomUUID().toString();

        EmployeeInsurance insurance = EmployeeInsurance.builder()
                .insuranceId(idGeneratorFactory.generateInsuranceId())
                .employee(employee)
                .policyNumber(request.getPolicyNumber())
                .policyNumberUnique(uniquePolicyNumber)
                .insuranceType(InsuranceType.valueOf(request.getInsuranceType()))
                .providerName(request.getProviderName())
                .coverageAmount(request.getCoverageAmount())
                .policyStartDate(request.getPolicyStartDate())
                .policyEndDate(request.getPolicyEndDate())
                .premiumAmount(request.getPremiumAmount())
                .beneficiaryName(request.getBeneficiaryName())
                .beneficiaryRelationship(request.getBeneficiaryRelationship())
                .beneficiaryPhone(request.getBeneficiaryPhone())
                .beneficiaryEmail(request.getBeneficiaryEmail())
                .status(InsuranceStatus.ACTIVE)
                .build();

        insurance = employeeInsuranceRepository.save(insurance);
        log.info("Insurance added successfully: {}", insurance.getInsuranceId());

        return mapToResponse(insurance);
    }

    /**
     * Get all insurances for an employee
     */
    public List<EmployeeInsuranceResponse> getEmployeeInsurances(String employeeId) {
        log.info("Fetching insurances for employee: {}", employeeId);
        List<EmployeeInsurance> insurances = employeeInsuranceRepository.findByEmployeeId(employeeId);
        return insurances.stream().map(this::mapToResponse).toList();
    }

    /**
     * Get active insurances for an employee
     */
    public List<EmployeeInsuranceResponse> getActiveInsurances(String employeeId) {
        log.info("Fetching active insurances for employee: {}", employeeId);
        List<EmployeeInsurance> insurances = employeeInsuranceRepository.findByEmployeeIdAndStatus(
                employeeId, InsuranceStatus.ACTIVE);
        return insurances.stream().map(this::mapToResponse).toList();
    }

    /**
     * Get insurances by type
     */
    public List<EmployeeInsuranceResponse> getInsurancesByType(String employeeId, String insuranceType) {
        log.info("Fetching {} insurances for employee: {}", insuranceType, employeeId);
        List<EmployeeInsurance> insurances = employeeInsuranceRepository.findByEmployeeIdAndInsuranceType(
                employeeId, InsuranceType.valueOf(insuranceType));
        return insurances.stream().map(this::mapToResponse).toList();
    }

    /**
     * Update insurance status
     */
    public EmployeeInsuranceResponse updateInsuranceStatus(String insuranceId, String status) {
        log.info("Updating insurance status: {}", insuranceId);

        EmployeeInsurance insurance = employeeInsuranceRepository.findById(insuranceId)
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.INSURANCE_NOT_FOUND));

        insurance.setStatus(InsuranceStatus.valueOf(status));
        insurance = employeeInsuranceRepository.save(insurance);

        log.info("Insurance status updated: {}", insuranceId);
        return mapToResponse(insurance);
    }

    /**
     * File an insurance claim
     */
    public EmployeeInsuranceResponse fileInsuranceClaim(String insuranceId, Double claimAmount, String claimDetails) {
        log.info("Filing claim for insurance: {}", insuranceId);

        EmployeeInsurance insurance = employeeInsuranceRepository.findById(insuranceId)
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.INSURANCE_NOT_FOUND));

        insurance.setClaimAmount(claimAmount);
        insurance.setClaimDetails(claimDetails);
        insurance.setClaimDate(java.time.LocalDate.now());
        insurance.setStatus(InsuranceStatus.CLAIM_PENDING);

        insurance = employeeInsuranceRepository.save(insurance);
        log.info("Insurance claim filed: {}", insuranceId);

        return mapToResponse(insurance);
    }

    /**
     * Get insurance by policy number
     */
    public EmployeeInsuranceResponse getInsuranceByPolicyNumber(String policyNumber) {
        log.info("Fetching insurance by policy number: {}", policyNumber);

        EmployeeInsurance insurance = employeeInsuranceRepository.findByPolicyNumberUnique(policyNumber)
                .orElseThrow(() -> new RuntimeException("Insurance policy not found"));

        return mapToResponse(insurance);
    }

    /**
     * Delete insurance
     */
    public void deleteInsurance(String insuranceId) {
        log.info("Deleting insurance: {}", insuranceId);

        EmployeeInsurance insurance = employeeInsuranceRepository.findById(insuranceId)
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.INSURANCE_NOT_FOUND));

        insurance.setStatus(InsuranceStatus.INACTIVE);
        employeeInsuranceRepository.save(insurance);

        log.info("Insurance deleted: {}", insuranceId);
    }

    private EmployeeInsuranceResponse mapToResponse(EmployeeInsurance insurance) {
        return EmployeeInsuranceResponse.builder()
                .insuranceId(insurance.getInsuranceId())
                .employeeId(insurance.getEmployee().getEmployeeId())
                .policyNumber(insurance.getPolicyNumber())
                .insuranceType(insurance.getInsuranceType().toString())
                .providerName(insurance.getProviderName())
                .coverageAmount(insurance.getCoverageAmount())
                .policyStartDate(insurance.getPolicyStartDate())
                .policyEndDate(insurance.getPolicyEndDate())
                .premiumAmount(insurance.getPremiumAmount())
                .beneficiaryName(insurance.getBeneficiaryName())
                .beneficiaryRelationship(insurance.getBeneficiaryRelationship())
                .beneficiaryPhone(insurance.getBeneficiaryPhone())
                .beneficiaryEmail(insurance.getBeneficiaryEmail())
                .status(insurance.getStatus().toString())
                .claimDetails(insurance.getClaimDetails())
                .claimDate(insurance.getClaimDate())
                .claimAmount(insurance.getClaimAmount())
                .build();
    }

}
