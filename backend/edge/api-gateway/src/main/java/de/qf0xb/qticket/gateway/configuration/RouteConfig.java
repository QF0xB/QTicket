package de.qf0xb.qticket.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class RouteConfig {
    @Bean
    public RouterFunction<ServerResponse> routes() {
        return route("auth_route")
                //.route(path("/api/{version:v[0-9]+}/auth/**"), http())
                .route(path("/api/{version:v[0-9]+}/auth/**"), http())
                .filter(lb("auth-service"))
                .build();
    }
}
