package de.qf0xb.qticket.user.config;

import de.qf0xb.qticket.auth.v1.client.api.AccountsApi;
import de.qf0xb.qticket.auth.v1.client.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.web.client.RestTemplate;

/**
 * Configures the Accounts API client for calling auth-service.
 * Uses a load-balanced RestTemplate so "auth-service" is resolved via Eureka,
 * and mTLS (user-service client cert) so auth-service can verify the caller (CN=user-service).
 *
 * Usage: inject {@link AccountsApi} and call e.g.
 * <pre>
 *   CreateAccountRequest req = new CreateAccountRequest()
 *       .userId(userId)
 *       .email(email)
 *       .username(username)
 *       .password(password);
 *   ResponseEntity<AuthAccount> created = accountsApi.createAccount(req);
 * </pre>
 */
@Configuration
public class AuthServiceClientConfig {

    @Bean
    RestTemplate authServiceRestTemplate(
            @LoadBalanced RestTemplateBuilder builder,
            SslBundles sslBundles,
            @Value("${qticket.auth-service.base-url:https://auth-service}") String baseUrl) {
        return builder
                .sslBundle(sslBundles.getBundle("user-service"))
                .rootUri(baseUrl)
                .build();
    }

    @Bean
    ApiClient authServiceApiClient(RestTemplate authServiceRestTemplate) {
        return new ApiClient(authServiceRestTemplate);
    }

    @Bean
    AccountsApi accountsApi(ApiClient authServiceApiClient) {
        return new AccountsApi(authServiceApiClient);
    }
}
