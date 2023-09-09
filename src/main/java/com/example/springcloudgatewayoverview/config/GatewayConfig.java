package com.example.springcloudgatewayoverview.config;

import com.example.springcloudgatewayoverview.filter.AuthFilter;
import com.example.springcloudgatewayoverview.filter.PostGlobalFilter;
import com.example.springcloudgatewayoverview.filter.RequestFilter;
import com.example.springcloudgatewayoverview.model.Company;
import com.example.springcloudgatewayoverview.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.WebFilter;

@Configuration
public class GatewayConfig {

    @Autowired
    RequestFilter requestFilter;

    @Autowired
    AuthFilter authFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
                // adding 2 rotes to first microservice as we need to log request body if method is POST
        return builder.routes()
                .route("first-microservice",r -> r.path("/first")
                        .and().method("POST")
                        .and().readBody(Student.class, s -> true).filters(f -> f.filters(requestFilter, authFilter))
                        .uri("http://localhost:8081"))
                .route("first-microservice",r -> r.path("/first")
                        .and().method("GET").filters(f-> f.filters(authFilter))
                        .uri("http://localhost:8081"))
                .route("second-microservice",r -> r.path("/second")
                        .and().method("POST")
                        .and().readBody(Company.class, s -> true).filters(f -> f.filters(requestFilter, authFilter))
                        .uri("http://localhost:8082"))

                .route("second-microservice",r -> r.path("/second")
                        .and().method("GET").filters(f-> f.filters(authFilter))
                        .uri("http://localhost:8082"))
                .route("auth-server",r -> r.path("/login")
                        .uri("http://localhost:8088"))
                .build();
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebFilter responseFilter(){
        return new PostGlobalFilter();
    }

}
