package com.example.multitenant.customer;

import com.example.multitenant.config.DataSourceType;
import com.example.multitenant.config.TenantContextHolder;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Customer createCustomer(String tenant, String name) {
        return executeInContext(tenant, DataSourceType.WRITE, () -> repository.save(name));
    }

    @Transactional(readOnly = true)
    public List<Customer> findAll(String tenant) {
        return executeInContext(tenant, DataSourceType.READ, repository::findAll);
    }

    @Transactional
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
