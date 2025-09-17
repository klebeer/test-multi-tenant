package com.example.multitenant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TenantRoutingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenantRoutingApplication.class, args);
    }
}
