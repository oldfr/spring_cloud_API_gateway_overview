package com.example.springcloudgatewayoverview.filter;
/*

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
//import org.springframework.web.server.ServerWebExchangeUtils;
import reactor.core.publisher.Mono;
@Component
public class ResponseBodyFilter implements GatewayFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerWebExchangeDecorator decoratedExchange = new ServerWebExchangeDecorator(exchange) {
            @Override
            public Mono<Void> writeWith(org.reactivestreams.Publisher<? extends org.springframework.core.io.buffer.DataBuffer> body) {
                // Read the response body here and perform any required operations
                // For example, you can log the body or modify it before returning it to the client

                // Use ServerWebExchangeUtils to read the response body
                ServerWebExchangeUtils.cacheRequestBody(exchange, (serverHttpRequest) -> {
                    // Manipulate the response body here
                    return Mono.just(serverHttpRequest);
                });

                return super.writeWith(body);
            }
        };

        return chain.filter(decoratedExchange);
    }

    @Override
    public int getOrder() {
        // Set the order of the filter
        // Lower values have higher priority
        return -1;
    }
}
*/
