package com.example.multitenant.customer;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.multitenant.config.DataSourceType;
import com.example.multitenant.config.TenantConnectionKey;
import com.example.multitenant.config.TenantConnectionRegistry;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CustomerServiceIntegrationTest {

    @Autowired
    private CustomerService service;

    @Autowired
    private TenantConnectionRegistry registry;

    @BeforeEach
    void setup() {
        registry.clear();
        service.deleteAll("tenant1");
        service.deleteAll("tenant2");
        registry.clear();
    }

    @AfterEach
    void tearDown() {
        registry.clear();
    }

    @Test
    void keepsDataIsolatedPerTenant() {
        service.createCustomer("tenant1", "Alice");
        service.createCustomer("tenant2", "Bob");

        List<Customer> tenant1Customers = service.findAll("tenant1");
        List<Customer> tenant2Customers = service.findAll("tenant2");

        assertThat(tenant1Customers).extracting(Customer::name).containsExactly("Alice");
        assertThat(tenant2Customers).extracting(Customer::name).containsExactly("Bob");

        List<TenantConnectionKey> keys = registry.getRecordedKeys();
        assertThat(keys).contains(TenantConnectionKey.of("tenant1", DataSourceType.WRITE));
        assertThat(keys).contains(TenantConnectionKey.of("tenant2", DataSourceType.WRITE));
        assertThat(keys).contains(TenantConnectionKey.of("tenant1", DataSourceType.READ));
        assertThat(keys).contains(TenantConnectionKey.of("tenant2", DataSourceType.READ));
    }

    @Test
    void switchesBetweenReadAndWriteConnections() {
        registry.clear();
        service.createCustomer("tenant1", "Alice");
        List<TenantConnectionKey> writeKeys = registry.getRecordedKeys();
        assertThat(writeKeys).contains(TenantConnectionKey.of("tenant1", DataSourceType.WRITE));

        registry.clear();
        List<Customer> customers = service.findAll("tenant1");
        assertThat(customers).hasSize(1);
        assertThat(registry.getRecordedKeys()).contains(TenantConnectionKey.of("tenant1", DataSourceType.READ));
    }
}
