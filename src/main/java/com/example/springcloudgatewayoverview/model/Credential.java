package com.example.springcloudgatewayoverview.model;

public class Credential {

    private String userName;
    private String role;

    public Credential() {
    }

    public Credential(String userName, String role) {
        this.userName = userName;
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
