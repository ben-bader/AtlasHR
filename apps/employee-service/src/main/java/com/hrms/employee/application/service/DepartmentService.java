package com.hrms.employee.application.service;

import com.hrms.employee.application.dto.*;
import com.hrms.employee.common.constants.ApplicationConstants;
import com.hrms.employee.common.utils.IdGeneratorFactory;
import com.hrms.employee.common.enums.DepartmentStatus;
import com.hrms.employee.domain.model.Department;
import com.hrms.employee.domain.model.Employee;
import com.hrms.employee.domain.repository.DepartmentRepository;
import com.hrms.employee.domain.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final IdGeneratorFactory idGeneratorFactory;

    public DepartmentService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository, IdGeneratorFactory idGeneratorFactory) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.idGeneratorFactory = idGeneratorFactory;
    }

    public DepartmentResponse createDepartment(CreateDepartmentRequest request) {
        log.info("Creating department: {}", request.getDepartmentName());

        Department department = Department.builder()
                .departmentId(idGeneratorFactory.generateDepartmentId())
                .departmentName(request.getDepartmentName())
                .description(request.getDescription())
                .departmentCode(request.getDepartmentCode())
                .status(DepartmentStatus.ACTIVE)
                .build();

        if (request.getDepartmentHead() != null) {
            Employee departmentHead = employeeRepository.findById(request.getDepartmentHead())
                    .orElseThrow(() -> new RuntimeException(ApplicationConstants.DEPARTMENT_HEAD_NOT_FOUND));
            department.setDepartmentHead(departmentHead);
        }

        if (request.getParentDepartmentId() != null) {
            Department parentDept = departmentRepository.findById(request.getParentDepartmentId())
                    .orElseThrow(() -> new RuntimeException(ApplicationConstants.PARENT_DEPARTMENT_NOT_FOUND));
            department.setParentDepartment(parentDept);
        }

        department = departmentRepository.save(department);
        log.info("Department created: {}", department.getDepartmentId());

        return mapToResponse(department);
    }

    public DepartmentResponse updateDepartment(Long departmentId, CreateDepartmentRequest request) {
        log.info("Updating department: {}", departmentId);

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.DEPARTMENT_NOT_FOUND));

        department.setDepartmentName(request.getDepartmentName());
        department.setDescription(request.getDescription());
        
        if (request.getDepartmentHead() != null) {
            Employee departmentHead = employeeRepository.findById(request.getDepartmentHead())
                    .orElseThrow(() -> new RuntimeException(ApplicationConstants.DEPARTMENT_HEAD_NOT_FOUND));
            department.setDepartmentHead(departmentHead);
        }

        department = departmentRepository.save(department);
        return mapToResponse(department);
    }

    public DepartmentResponse getDepartmentById(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.DEPARTMENT_NOT_FOUND));
        return mapToResponse(department);
    }

    public List<DepartmentResponse> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream().map(this::mapToResponse).toList();
    }

    public List<DepartmentResponse> getSubDepartments(Long parentDepartmentId) {
        List<Department> departments = departmentRepository.findByParentDepartment_DepartmentId(parentDepartmentId);
        return departments.stream().map(this::mapToResponse).toList();
    }

    public void deleteDepartment(Long departmentId) {
        log.info("Deleting department: {}", departmentId);
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.DEPARTMENT_NOT_FOUND));
        department.setStatus(DepartmentStatus.ARCHIVED);
        departmentRepository.save(department);
    }

    private DepartmentResponse mapToResponse(Department department) {
        DepartmentResponse response = new DepartmentResponse();
        response.setDepartmentId(department.getDepartmentId());
        response.setDepartmentName(department.getDepartmentName());
        response.setDescription(department.getDescription());
        response.setDepartmentCode(department.getDepartmentCode());
        response.setStatus(department.getStatus().toString());
        if (department.getParentDepartment() != null) {
            response.setParentDepartmentId(department.getParentDepartment().getDepartmentId());
        }
        if (department.getDepartmentHead() != null) {
            response.setDepartmentHead(department.getDepartmentHead().getFirstName() + " " + department.getDepartmentHead().getLastName());
        }
        return response;
    }

}
