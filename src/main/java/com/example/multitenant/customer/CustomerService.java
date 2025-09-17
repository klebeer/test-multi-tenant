package com.example.multitenant.customer;

import com.example.multitenant.config.DataSourceType;
import com.example.multitenant.config.TenantContextHolder;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public Customer createCustomer(String tenant, String name) {
        return executeInContext(tenant, DataSourceType.WRITE, () -> repository.save(name));
    }

    public List<Customer> findAll(String tenant) {
        return executeInContext(tenant, DataSourceType.READ, repository::findAll);
    }

    public void deleteAll(String tenant) {
        executeInContext(tenant, DataSourceType.WRITE, () -> {
            repository.deleteAll();
            return null;
        });
    }

    private <T> T executeInContext(String tenant, DataSourceType type, Supplier<T> supplier) {
        TenantContextHolder.setTenant(tenant);
        TenantContextHolder.setDataSourceType(type);
        try {
            return supplier.get();
        } finally {
            TenantContextHolder.clear();
        }
    }
}
