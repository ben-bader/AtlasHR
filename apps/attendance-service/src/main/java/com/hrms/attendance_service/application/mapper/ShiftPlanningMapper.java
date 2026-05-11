package com.hrms.attendance_service.application.mapper;

import com.hrms.attendance_service.application.dto.ShiftPlanningRequestDTO;
import com.hrms.attendance_service.application.dto.ShiftPlanningResponseDTO;
import com.hrms.attendance_service.domain.model.Shift;
import com.hrms.attendance_service.domain.model.ShiftPlanning;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShiftPlanningMapper {

    @Mapping(source = "shift.id", target = "shiftId")
    @Mapping(source = "shift.name", target = "shiftName")
    ShiftPlanningResponseDTO toDto(ShiftPlanning entity);

    @Mapping(target = "shift", expression = "java(mapShift(dto.getShiftId()))")
    ShiftPlanning toEntity(ShiftPlanningRequestDTO dto);

    default Shift mapShift(Long shiftId) {
        if (shiftId == null) return null;

        Shift shift = new Shift();
        shift.setId(shiftId);
        return shift;
    }
}
