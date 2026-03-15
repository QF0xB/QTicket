package de.qf0xb.qticket.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class RouteConfig {

    private static final String AUTH_PATH_PREFIX = "/api/{version:v[0-9]+}/auth";

    /**
     * Auth route: forward to auth-service, but do NOT route POST /api/v1/auth/accounts.
     * That endpoint is internal-only (create account); only user-service may call auth-service
     * directly via mTLS. The gateway must not expose it.
     */
    @Bean
    public RouterFunction<ServerResponse> routes() {
        var authPath = path(AUTH_PATH_PREFIX + "/**");
        var postCreateAccount = RequestPredicates.method(HttpMethod.POST)
                .and(path(AUTH_PATH_PREFIX + "/accounts"));

        return route("auth_route")
                .route(authPath.and(postCreateAccount.negate()), http())
                .filter(lb("auth-service"))
                .build();
    }
}
