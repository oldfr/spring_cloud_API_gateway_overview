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
               /* .route("api-internal-process-microservice",r -> r.path("/process").filters(f -> f.modifyRequestBody(ArrayList.class,
                        Object.class,
                        (exchange, reqMessage) -> {
                            try {
                                System.out.println(">>> INCOMING REQUEST <<< - "+reqMessage);
                                //Get query params
                                exchange.getRequest().getBody();
                                // In case of any validation errors, throw an exception so that
                                // it can be handled by a global exception handler
                                return Mono.just(reqMessage);
                            } catch (Exception e) {
                                System.out.println("Exception while modifying request body "+ e);
                                throw new RuntimeException(e.getMessage());
                            }
                        }))
                       .uri("http://localhost:8081")
                )*/
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

    //post filter
/*    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        System.out.println("Global Post Filter executed");
                    }));
        };
    }*/

/*    @Bean
    @Primary
    public ModifyRequestBodyGatewayFilterFactory getModifyRequestBodyGatewayFilterFactory() {
        return new RequestFilter();
    }*/
    @Bean
    public WebFilter responseFilter(){
        return new PostGlobalFilter();
    }

    /*
    @Bean
public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    return builder.routes()
      .route("r1", r -> r.host("**.baeldung.com")
        .and()
        .path("/baeldung")
        .uri("http://baeldung.com"))
      .route(r -> r.host("**.baeldung.com")
        .and()
        .path("/myOtherRouting")
        .filters(f -> f.prefixPath("/myPrefix"))
        .uri("http://othersite.com")
        .id("myOtherID"))
    .build();
}
     */
}
