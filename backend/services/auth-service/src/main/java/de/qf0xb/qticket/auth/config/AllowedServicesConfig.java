package de.qf0xb.qticket.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * Allowed service names (client certificate CN) for internal operations
 * such as creating accounts on behalf of users (e.g. user-service).
 */
@NullMarked
@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "qticket.auth")
public class AllowedServicesConfig {

    /**
     * Service names (X.509 client cert CN) allowed to call POST /accounts (create for another user).
     * Example: user-service.
     */
    private Set<String> allowedServiceNamesForAccountCreation = Set.of("user-service");
}