package com.example.multitenant.customer;

import com.example.multitenant.config.DataSourceType;
import com.example.multitenant.config.TenantContextHolder;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class CustomerService {

    private final CustomerRepository repository;
    private final TransactionTemplate writeTemplate;
    private final TransactionTemplate readTemplate;

    public CustomerService(CustomerRepository repository, PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.writeTemplate = new TransactionTemplate(transactionManager);
        this.readTemplate = new TransactionTemplate(transactionManager);
        this.readTemplate.setReadOnly(true);
    }

    public Customer createCustomer(String tenant, String name) {
        return executeInContext(tenant, DataSourceType.WRITE, writeTemplate,
                () -> repository.save(new Customer(null, name)));
    }

    public List<Customer> findAll(String tenant) {
        return executeInContext(tenant, DataSourceType.READ, readTemplate, repository::findAll);
    }

    public void deleteAll(String tenant) {
        executeInContext(tenant, DataSourceType.WRITE, writeTemplate, () -> {
            repository.deleteAll();
            return null;
        });
    }

    private <T> T executeInContext(
            String tenant, DataSourceType type, TransactionTemplate template, Supplier<T> supplier) {
        TenantContextHolder.setTenant(tenant);
        TenantContextHolder.setDataSourceType(type);
        try {
            return template.execute(status -> supplier.get());
        } finally {
            TenantContextHolder.clear();
        }
    }
}
