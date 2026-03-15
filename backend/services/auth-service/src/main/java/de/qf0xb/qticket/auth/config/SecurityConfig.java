package de.qf0xb.qticket.auth.config;

import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.x509.SubjectX500PrincipalExtractor;

import java.util.List;

@NullMarked
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Authority granted to requests authenticated via X.509 client certificate
     * (service-to-service mTLS). Principal is the cert CN (e.g. user-service).
     */
    public static final String X509_SERVICE_AUTHORITY = "SERVICE";

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            JwtDecoder jwtDecoder,
                                            JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/login/2fa",
                                "/api/v1/auth/token/refresh",
                                "/api/v1/auth/logout",
                                "/error",
                                "/api/v1/auth/.well-known/jwks.json",
                                "/actuator/health",
                                "/h2-console/**",
                                "/actuator/info")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/accounts").permitAll()
                        .anyRequest().authenticated())
                .x509(x509 -> x509
                        .x509PrincipalExtractor(new SubjectX500PrincipalExtractor())
                        .authenticationUserDetailsService(this::loadUserDetailsFromCertPrincipal))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)));
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)); // h2-console for now
        return http.build();
    }

    /**
     * Builds UserDetails from X.509 principal (cert CN). No DB lookup — used for
     * service identity; controller checks CN is in allowed-service list.
     */
    private UserDetails loadUserDetailsFromCertPrincipal(PreAuthenticatedAuthenticationToken token) {
        Object principal = token.getPrincipal();
        String name = principal instanceof String s ? s : principal.toString();
        return User.builder()
                .username(name)
                .password("")
                .authorities(List.of(new SimpleGrantedAuthority(X509_SERVICE_AUTHORITY)))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
