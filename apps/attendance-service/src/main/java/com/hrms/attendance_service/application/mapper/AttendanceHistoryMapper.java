package com.hrms.attendance_service.application.mapper;

import com.hrms.attendance_service.application.dto.AttendanceHistoryDTO;
import com.hrms.attendance_service.domain.model.AttendanceHistory;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttendanceHistoryMapper {

    @Mapping(source = "attendance.id", target = "attendanceId")
    AttendanceHistoryDTO toDto( AttendanceHistory entity );
}
