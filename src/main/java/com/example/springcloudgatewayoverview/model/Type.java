package com.example.springcloudgatewayoverview.model;

import java.util.List;

public class Type {

    private List<String> types;

    public Type() {

    }

    public Type(List<String> types) {
        this.types = types;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> type) {
        this.types = type;
    }
}
