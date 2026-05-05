package com.hrms.employee.domain.repository;

import com.hrms.employee.domain.model.EmployeeInsurance;
import com.hrms.employee.common.enums.InsuranceStatus;
import com.hrms.employee.common.enums.InsuranceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeInsuranceRepository extends JpaRepository<EmployeeInsurance, String> {
    List<EmployeeInsurance> findByEmployeeId(String employeeId);
    List<EmployeeInsurance> findByEmployeeIdAndStatus(String employeeId, InsuranceStatus status);
    List<EmployeeInsurance> findByEmployeeIdAndInsuranceType(String employeeId, InsuranceType insuranceType);
    Optional<EmployeeInsurance> findByPolicyNumberUnique(String policyNumberUnique);
    List<EmployeeInsurance> findByStatus(InsuranceStatus status);
    List<EmployeeInsurance> findByProviderName(String providerName);
}
