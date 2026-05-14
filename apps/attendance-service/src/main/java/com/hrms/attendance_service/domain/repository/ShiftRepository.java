package com.hrms.attendance_service.domain.repository;

import com.hrms.attendance_service.domain.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    Optional<Shift> findByName(String name);

    List<Shift> findByActiveTrue();

    List<Shift> findByActiveTrueAndDeletedFalse();
}
