package com.hrms.attendance_service.application.mapper;

import com.hrms.attendance_service.application.dto.AttendanceResponseDTO;
import com.hrms.attendance_service.domain.model.Attendance;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    @Mapping(source = "shift.id", target = "shiftId")
    @Mapping(source = "shift.name", target = "shiftName")
    AttendanceResponseDTO toResponse(Attendance attendance);
}
