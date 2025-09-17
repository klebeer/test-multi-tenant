package com.example.multitenant.web;

import com.example.multitenant.domain.Employee;
import com.example.multitenant.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> list() {
        return employeeService.findAll();
    }

    @GetMapping("/role/{role}")
    public List<Employee> byRole(@PathVariable String role) {
        return employeeService.findByRole(role);
    }

    @PostMapping
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        Employee created = employeeService.create(employee);
        return ResponseEntity.created(URI.create("/api/employees/" + created.getId())).body(created);
    }
}
