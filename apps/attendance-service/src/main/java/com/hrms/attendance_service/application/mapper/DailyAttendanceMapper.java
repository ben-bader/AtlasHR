package com.hrms.attendance_service.application.mapper;

import com.hrms.attendance_service.application.dto.DailyAttendanceDTO;
import com.hrms.attendance_service.domain.model.DailyAttendance;

import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = AttendanceMapper.class
)
public interface DailyAttendanceMapper {

    DailyAttendanceDTO toDto(DailyAttendance entity);
}
