package com.hrms.attendance_service.application.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.hrms.attendance_service.application.dto.BulkAttendanceDTO;
import com.hrms.attendance_service.domain.model.Attendance;

@Mapper(componentModel = "spring")
public interface BulkAttendanceMapper {

    Attendance toEntity(BulkAttendanceDTO dto);

    List<Attendance> toEntityList(List<BulkAttendanceDTO> dtos);
}
