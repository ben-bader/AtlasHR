package com.hrms.common.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.PropertySource;

/**
 * Loads hrms-common-defaults.properties into the Spring Environment with lower
 * priority than any service-specific application.properties.
 *
 * How priority works:
 *   application.properties  ← higher priority (wins on any key conflict)
 *   hrms-common-defaults     ← lower priority (@PropertySource is processed after)
 *
 * A service can override any default simply by declaring the same key in its
 * own application.properties — no special configuration required.
 */
@AutoConfiguration
@PropertySource(value = "classpath:hrms-common-defaults.properties")
public class HrmsDefaultsAutoConfig {
    // No beans. This class exists solely to register the @PropertySource.
}
