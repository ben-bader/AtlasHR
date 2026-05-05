package com.hrms.employee.application.service;

import com.hrms.employee.application.dto.*;
import com.hrms.employee.common.constants.ApplicationConstants;
import com.hrms.employee.common.utils.IdGeneratorFactory;
import com.hrms.employee.common.enums.DesignationStatus;
import com.hrms.employee.domain.model.Designation;
import com.hrms.employee.domain.repository.DesignationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class DesignationService {

    private final DesignationRepository designationRepository;
    private final IdGeneratorFactory idGeneratorFactory;

    public DesignationService(DesignationRepository designationRepository, IdGeneratorFactory idGeneratorFactory) {
        this.designationRepository = designationRepository;
        this.idGeneratorFactory = idGeneratorFactory;
    }

    public DesignationResponse createDesignation(CreateDesignationRequest request) {
        log.info("Creating designation: {}", request.getDesignationName());

        Designation designation = Designation.builder()
                .designationId(idGeneratorFactory.generateDesignationId())
                .designationName(request.getDesignationName())
                .description(request.getDescription())
                .designationCode(request.getDesignationCode())
                .hierarchyLevel(request.getHierarchyLevel())
                .reportingDesignation(request.getReportingDesignation())
                .status(DesignationStatus.ACTIVE)
                .build();

        designation = designationRepository.save(designation);
        log.info("Designation created: {}", designation.getDesignationId());

        return mapToResponse(designation);
    }

    public DesignationResponse updateDesignation(String designationId, CreateDesignationRequest request) {
        log.info("Updating designation: {}", designationId);

        Designation designation = designationRepository.findById(designationId)
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.DESIGNATION_NOT_FOUND));

        designation.setDesignationName(request.getDesignationName());
        designation.setDescription(request.getDescription());
        designation.setHierarchyLevel(request.getHierarchyLevel());
        designation.setReportingDesignation(request.getReportingDesignation());

        designation = designationRepository.save(designation);
        return mapToResponse(designation);
    }

    public DesignationResponse getDesignationById(String designationId) {
        Designation designation = designationRepository.findById(designationId)
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.DESIGNATION_NOT_FOUND));
        return mapToResponse(designation);
    }

    public List<DesignationResponse> getAllDesignations() {
        List<Designation> designations = designationRepository.findAll();
        return designations.stream().map(this::mapToResponse).toList();
    }

    public void deleteDesignation(String designationId) {
        log.info("Deleting designation: {}", designationId);
        Designation designation = designationRepository.findById(designationId)
                .orElseThrow(() -> new RuntimeException("Designation not found"));
        designation.setStatus(DesignationStatus.DEPRECATED);
        designationRepository.save(designation);
    }

    private DesignationResponse mapToResponse(Designation designation) {
        return DesignationResponse.builder()
                .designationId(designation.getDesignationId())
                .designationName(designation.getDesignationName())
                .description(designation.getDescription())
                .designationCode(designation.getDesignationCode())
                .hierarchyLevel(designation.getHierarchyLevel())
                .reportingDesignation(designation.getReportingDesignation())
                .status(designation.getStatus().toString())
                .build();
    }

}
