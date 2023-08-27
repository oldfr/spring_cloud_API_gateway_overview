package com.example.springcloudgatewayoverview.controller;

import com.example.springcloudgatewayoverview.model.Company;
import com.example.springcloudgatewayoverview.model.Student;
import com.example.springcloudgatewayoverview.model.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/process")
public class TypeController {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping
    public String getType(@RequestBody Type type) {
        System.out.println("getting type");
        System.out.println("types:"+type.getTypes());
        type.getTypes().forEach(f-> {
            if(f.equals("Student")) {
                System.out.println("calling first microservice - student");
                HttpEntity<Student> request = new HttpEntity<>(
                        new Student(1, "Test", "Student"));
                restTemplate.exchange("http://localhost:8080/first", HttpMethod.POST, request, String.class);
            }
            if(f.equals("Company")) {
                System.out.println("calling second microservice - company");
                HttpEntity<Company> request = new HttpEntity<>(
                        new Company(1, "Test", "Company"));
                restTemplate.exchange("http://localhost:8080/second", HttpMethod.POST, request, String.class);
            }
        } );

        return "done";
    }

}
