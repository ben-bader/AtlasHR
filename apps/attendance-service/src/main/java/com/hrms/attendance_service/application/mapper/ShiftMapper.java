package com.hrms.attendance_service.application.mapper;

import com.hrms.attendance_service.application.dto.ShiftRequestDTO;
import com.hrms.attendance_service.application.dto.ShiftResponseDTO;
import com.hrms.attendance_service.domain.model.Shift;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShiftMapper {

    ShiftResponseDTO toDto(Shift shift);

    Shift toEntity(ShiftRequestDTO dto);

    default Shift fromId(Long id) {
        if (id == null) return null;

        Shift shift = new Shift();
        shift.setId(id);
        return shift;
    }
}
