package com.hrms.attendance_service.application.service;

import com.hrms.attendance_service.common.enums.VerificationMethod;
import com.hrms.common.exception.ResourceNotFoundException;
import com.hrms.attendance_service.domain.model.Shift;
import com.hrms.attendance_service.domain.repository.ShiftRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftService {

    private final ShiftRepository shiftRepository;

    
    public Shift createShift(Shift shift) {
        return shiftRepository.save(shift);
    }


    public List<Shift> getAllActiveShifts() {
        return shiftRepository.findByActiveTrueAndDeletedFalse();
    }


    public Shift getById(Long id) {
        return shiftRepository.findById(id)
                .filter(s -> !s.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));
    }


    public void deleteShift(Long id) {

        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));

        shift.setDeleted(true);
        shiftRepository.save(shift);
    }

    // ================= BUSINESS RULE =================
    public boolean isValidMethod(Shift shift, VerificationMethod method) {

        if (shift == null || shift.getDeleted()) {
            return false;
        }

        return shift.getVerificationMethods().contains(method);
    }
}
