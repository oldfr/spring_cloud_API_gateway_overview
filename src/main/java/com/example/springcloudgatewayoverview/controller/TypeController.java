package com.example.springcloudgatewayoverview.controller;

import com.example.springcloudgatewayoverview.model.Company;
import com.example.springcloudgatewayoverview.model.Student;
import com.example.springcloudgatewayoverview.model.Type;
import com.example.springcloudgatewayoverview.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/process")
public class TypeController {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping
    public String getType(@RequestBody Type type, ServerHttpRequest inRequest) {
        System.out.println("getting type");
        System.out.println("types:"+type.getTypes());
        type.getTypes().forEach(f-> {
            if(f.equals("Student")) {
                System.out.println("calling first microservice - student");
                HttpEntity<Student> request = new HttpEntity<>(
                        new Student(1, "Test", "Student"),setAuthHeader(inRequest.getHeaders().get("userName").toString(),inRequest.getHeaders().get("role").toString()));
                restTemplate.exchange("http://localhost:8080/first", HttpMethod.POST, request, String.class);
            }
            if(f.equals("Company")) {
                System.out.println("calling second microservice - company");
                HttpEntity<Company> request = new HttpEntity<>(
                        new Company(1, "Test", "Company"),setAuthHeader(inRequest.getHeaders().get("userName").toString(),inRequest.getHeaders().get("role").toString()));
                restTemplate.exchange("http://localhost:8080/second", HttpMethod.POST, request, String.class);
            }
        } );
        return "done";
    }

    private HttpHeaders setAuthHeader(String userName, String role) {
        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization","Bearer "+authUtil.getToken("testuser","admin"));
        headers.set("Authorization","Bearer "+authUtil.getToken(userName,role));
        return headers;
    }

}
