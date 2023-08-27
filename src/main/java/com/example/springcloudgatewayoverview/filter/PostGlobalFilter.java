package com.example.springcloudgatewayoverview.filter;

import com.example.springcloudgatewayoverview.model.Company;
import com.example.springcloudgatewayoverview.model.Student;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactivestreams.Publisher;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PostGlobalFilter implements WebFilter, Ordered {

    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        DataBufferFactory dataBufferFactory = response.bufferFactory();
       /* ByteBuffer byteBuffer = null;
        try {
            byteBuffer = Mono.from(request.getBody()).toFuture().get().asByteBuffer();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        byte[] bytes = new byte[byteBuffer.capacity()];
        while (byteBuffer.hasRemaining()) {
            byteBuffer.get(bytes);
        }
        Student student = null;
        try {
            student = new ObjectMapper().readValue(new String(bytes, Charset.forName("UTF-8")), Student.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Request Body: "+student);*/
//        return chain.filter(exchange);
        // log the request body
//        ServerHttpRequest decoratedRequest = getDecoratedRequest(exchange, request);
        // log the response body
        ServerHttpResponseDecorator decoratedResponse = getDecoratedResponse(path, response, request, dataBufferFactory);
//        return chain.filter(exchange.mutate().request(decoratedRequest).response(decoratedResponse).build());
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    private ServerHttpResponseDecorator getDecoratedResponse(String path, ServerHttpResponse response, ServerHttpRequest request, DataBufferFactory dataBufferFactory) {
        return new ServerHttpResponseDecorator(response) {

            @Override
            public Mono<Void> writeWith(final Publisher<? extends DataBuffer> body) {

                if (body instanceof Flux) {

                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;

                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {

                        DefaultDataBuffer joinedBuffers = new DefaultDataBufferFactory().join(dataBuffers);
                        byte[] content = new byte[joinedBuffers.readableByteCount()];
                        joinedBuffers.read(content);
                        String responseBody = new String(content, StandardCharsets.UTF_8);//MODIFY RESPONSE and Return the Modified response
                        System.out.println("requestId: "+request.getId()+", method: "+request.getMethodValue()+", req url: "+request.getURI()+", response body :"+ responseBody);
                        try {
                            if(request.getURI().getPath().equals("/first") && request.getMethodValue().equals("GET")) {
                                List<Student> student = new ObjectMapper().readValue(responseBody, List.class);
                                System.out.println("student:" + student);
                            }
                            else if(request.getURI().getPath().equals("/second") && request.getMethodValue().equals("GET")) {
                                List<Company> companies = new ObjectMapper().readValue(responseBody, List.class);
                                System.out.println("companies:" + companies);
                            }
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        return dataBufferFactory.wrap(responseBody.getBytes());
                    })).onErrorResume(err -> {

                        System.out.println("error while decorating Response: {}"+err.getMessage());
                        return Mono.empty();
                    });

                }
                return super.writeWith(body);
            }
        };
    }

    private ServerHttpRequest getDecoratedRequest(ServerWebExchange exchange, ServerHttpRequest request) {

        return new ServerHttpRequestDecorator(request) {
            @Override
            public Flux<DataBuffer> getBody() {
                System.out.println("reading request:");
               /* ByteBuffer byteBuffer = null;
                try {
                    byteBuffer = Mono.from(exchange.getRequest().getBody()).toFuture().get().asByteBuffer();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
                byte[] bytes = new byte[byteBuffer.capacity()];
                while (byteBuffer.hasRemaining()) {
                    byteBuffer.get(bytes);
                }
                Student student = null;
                try {
                    student = new ObjectMapper().readValue(new String(bytes, Charset.forName("UTF-8")), Student.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Request Body: "+student);*/
/*
                String req = DataBufferUtils.join(request.getBody())
                        .map(DataBuffer::asByteBuffer)
                        .map(ByteBuffer::array)
                        .map(String::new).toString();
//                req.flatMap(System.out::print);
//                ServerHttpRequestUtils
                System.out.println("reqTest:"+req);

                System.out.println("requestId:"+request.getId()+", method:"+request.getMethodValue()+" , url:"+ request.getURI()+" request body:"+request.getBody().toString());

                ServerWebExchangeUtils.cacheRequestBody(exchange, (serverHttpRequest) -> {
                    // Manipulate the response body here
//                exchange.getResponse()

                    return Mono.just(serverHttpRequest);});
//                Object student = exchange.getAttribute(CACHED_REQUEST_BODY_ATTR);
//                System.out.println("student:"+student);*/
                return super.getBody().publishOn(Schedulers.boundedElastic()).doOnNext(dataBuffer -> {

                    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

                        Channels.newChannel(byteArrayOutputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
//                        String requestBody = "test";//IOUtils.toString(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8.toString());//MODIFY REQUEST and Return the Modified request
//                        System.out.println("for requestId:"+request.getId()+", request body :{}"+ requestBody);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
            }
        };
    }
    //post filter
   /* @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("in postfilter");
        *//*ServerWebExchangeDecorator decoratedExchange = new ServerHttpResponseDecorator(exchange) {

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
        };*//*
        return chain.filter(exchange).then(Mono.fromRunnable(()->{
            ServerHttpResponse response = exchange.getResponse();
            HttpStatus responseStatus = response.getStatusCode();
            System.out.println("response:"+response);
            DataBufferFactory buffer = response.bufferFactory();
            //to read req body
            *//*ServerWebExchangeUtils.cacheRequestBody(exchange, (serverHttpRequest) -> {
                // Manipulate the response body here
//                exchange.getResponse()
                return Mono.just(serverHttpRequest);
            });
            Student student = exchange.getAttribute(CACHED_REQUEST_BODY_ATTR);
            System.out.println("student:"+student);*//*
            if(responseStatus.equals(HttpStatus.OK)){
                String newResponseBody =
                        "<body>\n" +
                                "      <h1 style=\"color:red;text-align:center\">Bad Request </h1>\n" +
                                "      <p>If you are seeing this page it means response body is modified.</p>\n" +
                                "  </body>";

                DataBuffer dataBuffer = response.bufferFactory().wrap(newResponseBody.getBytes(StandardCharsets.UTF_8));
                response.writeWith(Mono.just(dataBuffer)).subscribe();
                exchange.mutate().response(response).build();
            }
        }));
    }*/

    @Override
    public int getOrder() {
        return 0;
    }

}
