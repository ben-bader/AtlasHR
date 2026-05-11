package com.hrms.attendance_service.application.mapper;

import com.hrms.attendance_service.application.dto.AttendanceResponse;
import com.hrms.attendance_service.domain.model.Attendance;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    @Mapping(source = "shift.id", target = "shiftId")
    @Mapping(source = "shift.name", target = "shiftName")
    @Mapping(source = "method", target = "method")
    AttendanceResponseDTO toResponse(Attendance attendance);
}