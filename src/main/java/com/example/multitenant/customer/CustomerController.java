package com.example.multitenant.customer;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/{tenant}/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public List<Customer> getCustomers(@PathVariable String tenant) {
        return service.findAll(tenant);
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@PathVariable String tenant, @Valid @RequestBody CustomerRequest request) {
        Customer created = service.createCustomer(tenant, request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
