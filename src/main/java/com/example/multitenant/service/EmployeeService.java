package com.example.multitenant.service;

import com.example.multitenant.domain.Employee;
import com.example.multitenant.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Employee> findByRole(String role) {
        return employeeRepository.findByRole(role);
    }

    @Transactional
    public Employee create(Employee employee) {
        return employeeRepository.save(employee);
    }
}
