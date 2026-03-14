package de.qf0xb.qticket.auth.config;

import de.qf0xb.qticket.user.v1.client.api.UserApi;
import de.qf0xb.qticket.user.v1.client.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class UserClientConfig {
    @Bean
    RestTemplate userServiceRestTemplate(RestTemplateBuilder builder,
                                         SslBundles sslBundles /*,
                                         @Value("${qticket.user-service.base-url}") String baseUrl*/) {
        return builder
                .sslBundle(sslBundles.getBundle("auth-service"))
                .rootUri("https://localhost:8102")
                .build();
    }
    @Bean
    ApiClient userApiClient(RestTemplate userServiceRestTemplate) {
        // ApiClient will use the mTLS RestTemplate
        return new ApiClient(userServiceRestTemplate);
    }
    @Bean
    UserApi userApi(ApiClient userApiClient) {
        return new UserApi(userApiClient);
    }
}
