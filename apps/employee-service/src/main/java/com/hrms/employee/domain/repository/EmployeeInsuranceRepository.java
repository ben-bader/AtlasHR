package com.hrms.employee.domain.repository;

import com.hrms.employee.domain.model.EmployeeInsurance;
import com.hrms.employee.common.enums.InsuranceStatus;
import com.hrms.employee.common.enums.InsuranceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeInsuranceRepository extends JpaRepository<EmployeeInsurance, String> {

    @Query("SELECT ei FROM EmployeeInsurance ei WHERE ei.employee.id = :employeeId")
    List<EmployeeInsurance> findByEmployeeId(@Param("employeeId") String employeeId);

    @Query("SELECT ei FROM EmployeeInsurance ei WHERE ei.employee.id = :employeeId AND ei.status = :status")
    List<EmployeeInsurance> findByEmployeeIdAndStatus(@Param("employeeId") String employeeId, @Param("status") InsuranceStatus status);

    @Query("SELECT ei FROM EmployeeInsurance ei WHERE ei.employee.id = :employeeId AND ei.insuranceType = :insuranceType")
    List<EmployeeInsurance> findByEmployeeIdAndInsuranceType(@Param("employeeId") String employeeId, @Param("insuranceType") InsuranceType insuranceType);
    Optional<EmployeeInsurance> findByPolicyNumberUnique(String policyNumberUnique);
    List<EmployeeInsurance> findByStatus(InsuranceStatus status);
    List<EmployeeInsurance> findByProviderName(String providerName);
}
