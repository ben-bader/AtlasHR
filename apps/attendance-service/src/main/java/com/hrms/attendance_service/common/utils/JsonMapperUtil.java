package com.hrms.attendance_service.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonMapperUtil {

        private final ObjectMapper objectMapper;

        public <T> String toJson(T object) {

            try {
                return objectMapper.writeValueAsString(object);
            } catch (Exception e) {
                throw new IllegalArgumentException("JSON serialization failed", e);
            }
        }

        public <T> T fromJson(String json, Class<T> clazz) {

            try {
                return objectMapper.readValue(json, clazz);
            } catch (Exception e) {
                throw new IllegalArgumentException("JSON deserialization failed", e);
            }
        }
} 
