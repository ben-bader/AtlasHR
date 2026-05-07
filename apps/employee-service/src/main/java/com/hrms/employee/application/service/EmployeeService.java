package com.hrms.employee.application.service;

import com.hrms.employee.application.dto.*;
import com.hrms.employee.common.constants.ApplicationConstants;
import com.hrms.employee.common.enums.EmploymentChangeType;
import com.hrms.employee.common.enums.EmploymentStatus;
import com.hrms.employee.common.enums.InsuranceType;
import com.hrms.employee.common.utils.IdGeneratorFactory;
import com.hrms.employee.domain.model.*;
import com.hrms.employee.domain.repository.*;
import com.hrms.employee.infrastructure.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final EmploymentHistoryRepository employmentHistoryRepository;
    private final OrganizationChartRepository organizationChartRepository;
    private final EmployeeInsuranceRepository employeeInsuranceRepository;
    private final DomainEventPublisher eventPublisher;
    private final IdGeneratorFactory idGeneratorFactory;

    public EmployeeService(EmployeeRepository employeeRepository,
                          DepartmentRepository departmentRepository,
                          DesignationRepository designationRepository,
                          EmploymentHistoryRepository employmentHistoryRepository,
                          OrganizationChartRepository organizationChartRepository,
                          EmployeeInsuranceRepository employeeInsuranceRepository,
                          DomainEventPublisher eventPublisher,
                          IdGeneratorFactory idGeneratorFactory) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.designationRepository = designationRepository;
        this.employmentHistoryRepository = employmentHistoryRepository;
        this.organizationChartRepository = organizationChartRepository;
        this.employeeInsuranceRepository = employeeInsuranceRepository;
        this.eventPublisher = eventPublisher;
        this.idGeneratorFactory = idGeneratorFactory;
    }

    /**
     * Onboard a new employee
     */
    public EmployeeResponse onboardEmployee(CreateEmployeeRequest request) {
        log.info("Onboarding new employee: {}", request.getEmail());

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Designation designation = designationRepository.findById(request.getDesignationId())
                .orElseThrow(() -> new RuntimeException("Designation not found"));

        Employee reportingManager = null;
        if (request.getReportingManagerId() != null) {
            reportingManager = employeeRepository.findById(request.getReportingManagerId())
                    .orElseThrow(() -> new RuntimeException("Reporting manager not found"));
        }

        Employee employee = Employee.builder()
                .employeeId(idGeneratorFactory.generateEmployeeId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .CIN(request.getCIN())
                .personalInfo(PersonalInfo.builder()
                        .dateOfBirth(request.getDateOfBirth())
                        .gender(request.getGender())
                        .nationality(request.getNationality())
                        .bloodGroup(request.getBloodGroup())
                        .maritalStatus(request.getMaritalStatus())
                        .build())
                .contactInfo(ContactInfo.builder()
                        .primaryPhone(request.getPrimaryPhone())
                        .alternatePhone(request.getAlternatePhone())
                        .currentAddress(request.getCurrentAddress())
                        .city(request.getCity())
                        .province(request.getProvince())
                        .codePostal(request.getCodePostal())
                        .build())
                .bankDetails(BankDetails.builder()
                        .accountHolderName(request.getAccountHolderName())
                        .accountNumber(request.getAccountNumber())
                        .RIB(request.getRIB())
                        .bankName(request.getBankName())
                        .accountType(request.getAccountType())
                        .build())
                .emergencyContact(EmergencyContact.builder()
                        .name(request.getEmergencyContactName())
                        .relationship(request.getEmergencyContactRelationship())
                        .phoneNumber(request.getEmergencyContactPhone())
                        .email(request.getBeneficiaryEmail())
                        .build())
                .insuranceDetails(InsuranceDetails.builder()
                        .policyNumber(request.getPolicyNumber())
                        .insuranceType(request.getInsuranceType() != null ? InsuranceType.valueOf(request.getInsuranceType()) : null)
                        .providerName(request.getProviderName())
                        .coverageAmount(request.getCoverageAmount())
                        .policyStartDate(request.getPolicyStartDate())
                        .policyEndDate(request.getPolicyEndDate())
                        .premiumAmount(request.getPremiumAmount())
                        .beneficiaryName(request.getBeneficiaryName())
                        .beneficiaryRelationship(request.getBeneficiaryRelationship())
                        .build())
                .department(department)
                .designation(designation)
                .reportingManager(reportingManager)
                .joiningDate(request.getJoiningDate())
                .status(EmploymentStatus.ACTIVE)
                .grade(request.getGrade())
                .build();

        employee = employeeRepository.save(employee);

        // Create organization chart entry
        createOrganizationChartEntry(employee, reportingManager);

        // Publish event
        publishEmployeeCreatedEvent(employee);

        log.info("Employee onboarded successfully: {}", employee.getEmployeeId());

        return mapToResponse(employee);
    }

    /**
     * Promote an employee
     */
    public EmployeeResponse promoteEmployee(PromoteEmployeeRequest request) {
        log.info("Promoting employee: {}", request.getEmployeeId());

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.EMPLOYEE_NOT_FOUND));

        if (!employee.isActive()) {
            throw new RuntimeException("Cannot promote inactive employee");
        }

        Designation newDesignation = designationRepository.findById(request.getNewDesignationId())
                .orElseThrow(() -> new RuntimeException("New designation not found"));

        // Record promotion in employment history
        recordEmploymentChange(employee, EmploymentChangeType.PROMOTION,
                employee.getDesignation(),
                newDesignation,
                null,
                null,
                employee.getGrade(),
                request.getNewGrade(),
                request.getReason());

        employee.setDesignation(newDesignation);
        employee.setGrade(request.getNewGrade());
        employee = employeeRepository.save(employee);

        // Publish transfer event (salary revision notification)
        publishEmployeeUpdatedEvent(employee);

        log.info("Employee promoted successfully: {}", employee.getEmployeeId());

        return mapToResponse(employee);
    }

    /**
     * Transfer an employee to another department/designation
     */
    public EmployeeResponse transferEmployee(TransferEmployeeRequest request) {
        log.info("Transferring employee: {}", request.getEmployeeId());

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.EMPLOYEE_NOT_FOUND));

        if (!employee.isActive()) {
            throw new RuntimeException("Cannot transfer inactive employee");
        }

        Department newDepartment = departmentRepository.findById(request.getNewDepartmentId())
                .orElseThrow(() -> new RuntimeException("New department not found"));

        Designation newDesignation = employee.getDesignation();

        if (request.getNewDesignationId() != null) {
            newDesignation = designationRepository.findById(request.getNewDesignationId())
                    .orElseThrow(() -> new RuntimeException("New designation not found"));
        }

        // Record transfer in employment history
        recordEmploymentChange(employee, EmploymentChangeType.TRANSFER,
                employee.getDesignation(),
                newDesignation,
                employee.getDepartment(),
                newDepartment,
                employee.getGrade(),
                employee.getGrade(),
                request.getReason());

        employee.setDepartment(newDepartment);
        if (newDesignation != null) {
            employee.setDesignation(newDesignation);
        }
        employee = employeeRepository.save(employee);

        // Update organization chart
        updateOrganizationChartEntry(employee);

        // Publish transfer event
        publishEmployeeTransferredEvent(employee);

        log.info("Employee transferred successfully: {}", employee.getEmployeeId());

        return mapToResponse(employee);
    }

    /**
     * Terminate an employee
     */
    public EmployeeResponse terminateEmployee(TerminateEmployeeRequest request) {
        log.info("Terminating employee: {}", request.getEmployeeId());

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (employee.isTerminated()) {
            throw new RuntimeException("Employee already terminated");
        }

        employee.setStatus(EmploymentStatus.TERMINATED);
        employee.setLastWorkingDate(request.getLastWorkingDate());
        employee = employeeRepository.save(employee);

        // Publish termination event
        publishEmployeeTerminatedEvent(employee, request.getTerminationReason());

        log.info("Employee terminated successfully: {}", employee.getEmployeeId());

        return mapToResponse(employee);
    }

    /**
     * Get employee by ID
     */
    public EmployeeResponse getEmployeeById(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return mapToResponse(employee);
    }

    /**
     * Get all employees in a department
     */
    public List<EmployeeResponse> getEmployeesByDepartment(Long departmentId) {
        List<Employee> employees = employeeRepository.findEmployeesByDepartmentId(departmentId);
        return employees.stream().map(this::mapToResponse).toList();
    }

    /**
     * Get all active employees
     */
    public List<EmployeeResponse> getAllActiveEmployees() {
        List<Employee> employees = employeeRepository.findByStatus(EmploymentStatus.ACTIVE);
        return employees.stream().map(this::mapToResponse).toList();
    }

    /**
     * Get direct reports for a manager
     */
    public List<EmployeeResponse> getDirectReports(String managerId) {
        List<Employee> employees = employeeRepository.findByReportingManager_EmployeeId(managerId);
        return employees.stream().map(this::mapToResponse).toList();
    }

    /**
     * Update employee information
     */
    public EmployeeResponse updateEmployee(String employeeId, CreateEmployeeRequest request) {
        log.info("Updating employee: {}", employeeId);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        
        // Update contact info
        if (employee.getContactInfo() == null) {
            employee.setContactInfo(new ContactInfo());
        }
        employee.getContactInfo().setPrimaryPhone(request.getPrimaryPhone());
        employee.getContactInfo().setAlternatePhone(request.getAlternatePhone());
        employee.getContactInfo().setCurrentAddress(request.getCurrentAddress());
        employee.getContactInfo().setCity(request.getCity());
        employee.getContactInfo().setProvince(request.getProvince());
        employee.getContactInfo().setCodePostal(request.getCodePostal());

        employee = employeeRepository.save(employee);

        publishEmployeeUpdatedEvent(employee);

        return mapToResponse(employee);
    }

    // Private helper methods

    private void recordEmploymentChange(Employee employee, EmploymentChangeType changeType,
                                       Designation previousDesignation, Designation newDesignation,
                                       Department previousDepartment, Department newDepartment,
                                       String previousGrade, String newGrade,
                                       String reason) {
        EmploymentHistory history = EmploymentHistory.builder()
                .historyId(idGeneratorFactory.generateEmploymentHistoryId())
                .employee(employee)
                .effectiveDate(LocalDate.now())
                .changeType(changeType)
                .reason(reason)
                .build();

        if (changeType == EmploymentChangeType.PROMOTION || changeType == EmploymentChangeType.GRADE_CHANGE) {
            history.setPreviousDesignation(previousDesignation);
            history.setNewDesignation(newDesignation);
            history.setPreviousGrade(previousGrade);
            history.setNewGrade(newGrade);
        } else if (changeType == EmploymentChangeType.TRANSFER) {
            history.setPreviousDepartment(previousDepartment);
            history.setNewDepartment(newDepartment);
            history.setPreviousDesignation(previousDesignation);
            history.setNewDesignation(newDesignation);
            history.setPreviousGrade(previousGrade);
            history.setNewGrade(newGrade);
        }

        employmentHistoryRepository.save(history);
    }

    private void createOrganizationChartEntry(Employee employee, Employee manager) {
        OrganizationChart chart = OrganizationChart.builder()
                .chartId(idGeneratorFactory.generateOrganizationChartId())
                .employee(employee)
                .manager(manager)
                .hierarchyLevel(manager != null ? 2 : 1)
                .build();
        organizationChartRepository.save(chart);
    }

    private void updateOrganizationChartEntry(Employee employee) {
Optional<OrganizationChart> existingChart =
    organizationChartRepository.findByEmployee_EmployeeId(employee.getEmployeeId());   
         if (existingChart.isPresent()) {
            OrganizationChart chart = existingChart.get();
            organizationChartRepository.save(chart);
        }
    }

    private void publishEmployeeCreatedEvent(Employee employee) {
        EmployeeCreatedEvent event = EmployeeCreatedEvent.builder()
                .employeeId(employee.getEmployeeId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .departmentId(employee.getDepartment().getDepartmentId().toString())
                .designationId(employee.getDesignation().getDesignationId())
                .joiningDate(employee.getJoiningDate().toString())
                .build();
        eventPublisher.publishEmployeeCreatedEvent(event);
    }

    private void publishEmployeeUpdatedEvent(Employee employee) {
        EmployeeUpdatedEvent event = EmployeeUpdatedEvent.builder()
                .employeeId(employee.getEmployeeId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .departmentId(employee.getDepartment().getDepartmentId().toString())
                .designationId(employee.getDesignation().getDesignationId())
                .status(employee.getStatus().toString())
                .build();
        eventPublisher.publishEmployeeUpdatedEvent(event);
    }

    private void publishEmployeeTransferredEvent(Employee employee) {
        EmployeeTransferredEvent event = EmployeeTransferredEvent.builder()
                .employeeId(employee.getEmployeeId())
                .employeeName(employee.getFullName())
                .newDepartmentId(employee.getDepartment().getDepartmentId().toString())
                .newDesignation(employee.getDesignation().getDesignationName())
                .effectiveDate(LocalDateTime.now())
                .build();
        eventPublisher.publishEmployeeTransferredEvent(event);
    }

    private void publishEmployeeTerminatedEvent(Employee employee, String terminationReason) {
        EmployeeTerminatedEvent event = EmployeeTerminatedEvent.builder()
                .employeeId(employee.getEmployeeId())
                .employeeName(employee.getFullName())
                .email(employee.getEmail())
                .departmentId(employee.getDepartment().getDepartmentId().toString())
                .designationId(employee.getDesignation().getDesignationId())
                .lastWorkingDate(employee.getLastWorkingDate().toString())
                .terminationReason(terminationReason)
                .build();
        eventPublisher.publishEmployeeTerminatedEvent(event);
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        List<EmployeeInsurance> insuranceRecords = employeeInsuranceRepository.findByEmployeeId(employee.getEmployeeId());
        List<EmployeeInsuranceResponse> insuranceResponses = insuranceRecords.stream()
                .map(this::mapInsuranceToResponse).toList();

        return EmployeeResponse.builder()
                .employeeId(employee.getEmployeeId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .CIN(employee.getCIN())
                .joiningDate(employee.getJoiningDate())
                .dateOfBirth(employee.getPersonalInfo() != null ? employee.getPersonalInfo().getDateOfBirth() : null)
                .gender(employee.getPersonalInfo() != null ? employee.getPersonalInfo().getGender() : null)
                .nationality(employee.getPersonalInfo() != null ? employee.getPersonalInfo().getNationality() : null)
                .bloodGroup(employee.getPersonalInfo() != null ? employee.getPersonalInfo().getBloodGroup() : null)
                .maritalStatus(employee.getPersonalInfo() != null ? employee.getPersonalInfo().getMaritalStatus() : null)
                .status(employee.getStatus())
                .departmentName(employee.getDepartment().getDepartmentName())
                .departmentId(employee.getDepartment().getDepartmentId())
                .designationName(employee.getDesignation().getDesignationName())
                .designationId(employee.getDesignation().getDesignationId())
                .reportingManagerName(employee.getReportingManager() != null ? employee.getReportingManager().getFullName() : null)
                .reportingManagerId(employee.getReportingManager() != null ? employee.getReportingManager().getEmployeeId() : null)
                .grade(employee.getGrade())
                // Contact Information
                .primaryPhone(employee.getContactInfo() != null ? employee.getContactInfo().getPrimaryPhone() : null)
                .alternatePhone(employee.getContactInfo() != null ? employee.getContactInfo().getAlternatePhone() : null)
                .currentAddress(employee.getContactInfo() != null ? employee.getContactInfo().getCurrentAddress() : null)
                .city(employee.getContactInfo() != null ? employee.getContactInfo().getCity() : null)
                .province(employee.getContactInfo() != null ? employee.getContactInfo().getProvince() : null)
                .codePostal(employee.getContactInfo() != null ? employee.getContactInfo().getCodePostal() : null)
                // Bank Details
                .bankName(employee.getBankDetails() != null ? employee.getBankDetails().getBankName() : null)
                .accountNumber(employee.getBankDetails() != null ? employee.getBankDetails().getAccountNumber() : null)
                .RIB(employee.getBankDetails() != null ? employee.getBankDetails().getRIB() : null)
                .accountHolderName(employee.getBankDetails() != null ? employee.getBankDetails().getAccountHolderName() : null)
                .accountType(employee.getBankDetails() != null ? employee.getBankDetails().getAccountType() : null)
                // Insurance
                .insurances(insuranceResponses)
                .build();
    }

    private EmployeeInsuranceResponse mapInsuranceToResponse(EmployeeInsurance insurance) {
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
