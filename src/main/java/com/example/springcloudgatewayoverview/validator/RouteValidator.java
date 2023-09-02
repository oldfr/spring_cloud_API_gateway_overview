package com.example.springcloudgatewayoverview.validator;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    public static final List<String> unprotectedURLs = List.of("/login");

    public Predicate<ServerHttpRequest> isSecured = request -> unprotectedURLs.stream().noneMatch(uri -> request.getURI().getPath().contains(uri));
}
