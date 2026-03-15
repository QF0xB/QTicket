package de.qf0xb.qticket.auth.config;

import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Configuration;

@NullMarked
@Configuration
public class RestTemplateConfig {
    @Bean
    @LoadBalanced
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }
}
