package com.example.hello;

public class Contact {
    private String name;
    private String status;

    public Contact(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
}
