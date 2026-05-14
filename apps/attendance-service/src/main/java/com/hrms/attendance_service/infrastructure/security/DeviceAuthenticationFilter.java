package com.hrms.attendance_service.infrastructure.security;

import com.hrms.attendance_service.application.service.DeviceAuthService;
import com.hrms.attendance_service.domain.model.Device;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DeviceAuthenticationFilter extends OncePerRequestFilter {

    private final DeviceAuthService deviceService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // only protect attendance endpoints
        if (!path.startsWith("/api/attendance")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader("X-Device-Key");

        if (apiKey == null || apiKey.isBlank()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Missing Device API Key");
            return;
        }

        try {
            Device device = deviceService.validateDevice(apiKey);
            deviceService.updateLastSeen(device);

            // you can attach device to request
            request.setAttribute("device", device);

        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Invalid Device API Key");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
