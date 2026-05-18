package com.hrms.common.autoconfigure;

import com.hrms.common.exception.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for the shared GlobalExceptionHandler.
 * Skipped if the consuming service defines its own GlobalExceptionHandler bean.
 */
@AutoConfiguration
@ConditionalOnWebApplication
public class HrmsExceptionAutoConfig {

    @Bean
    @ConditionalOnMissingBean(GlobalExceptionHandler.class)
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
