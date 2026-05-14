package com.hrms.attendance_service.application.service;

import com.hrms.attendance_service.domain.model.Device;
import com.hrms.attendance_service.domain.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceAuthService {

    private final DeviceRepository deviceRepository;

    public Device validateDeviceKey(String apiKey) {

        return deviceRepository.findByApiKeyAndActiveTrue(apiKey)
                .orElseThrow(() -> new RuntimeException("Invalid device API key"));
    }

    public void updateLastSeen(Device device) {
        device.setLastSeen(java.time.LocalDateTime.now());
        deviceRepository.save(device);
    }
}
