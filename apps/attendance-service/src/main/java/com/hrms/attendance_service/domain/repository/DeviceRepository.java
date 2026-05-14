package com.hrms.attendance_service.domain.repository;

import com.hrms.attendance_service.domain.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByDeviceUid(String deviceUid);

    Optional<Device> findByApiKeyAndActiveTrue(String apiKey);

}
