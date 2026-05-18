package com.hrms.common.autoconfigure;

import com.hrms.common.security.GatewayTrustFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * Auto-configuration that exposes a GatewayTrustFilter bean for services that
 * have Spring Security on the classpath.
 *
 * Each service's SecurityConfig injects this bean and wires it into the filter chain:
 *   http.addFilterBefore(gatewayTrustFilter, UsernamePasswordAuthenticationFilter.class)
 */
@AutoConfiguration
@ConditionalOnClass(HttpSecurity.class)
public class HrmsSecurityAutoConfig {

    @Bean
    @ConditionalOnMissingBean(GatewayTrustFilter.class)
    public GatewayTrustFilter gatewayTrustFilter() {
        return new GatewayTrustFilter();
    }
}
