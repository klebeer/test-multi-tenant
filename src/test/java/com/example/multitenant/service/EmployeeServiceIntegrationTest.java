package com.example.multitenant.service;

import com.example.multitenant.config.TenantDataSourceProperties;
import com.example.multitenant.context.TenantContextHolder;
import com.example.multitenant.domain.Employee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EmployeeServiceIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TenantDataSourceProperties tenantDataSourceProperties;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @AfterEach
    void clearContext() {
        TenantContextHolder.clear();
    }

    @Test
    void shouldIsolateDataPerTenant() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);

        String tenant1 = tenantDataSourceProperties.getDatasources().keySet().iterator().next();
        String tenant2 = tenantDataSourceProperties.getDatasources().keySet().stream().skip(1).findFirst()
                .orElseThrow();

        TenantContextHolder.setTenantId(tenant1);
        List<Employee> tenant1Employees = template.execute(status -> employeeService.findAll());
        assertThat(tenant1Employees).isNotNull();
        int initialTenant1Count = tenant1Employees.size();

        TenantContextHolder.setTenantId(tenant1);
        template.execute(status -> {
            employeeService.create(new Employee("Carlos", "Lopez", "Designer"));
            return null;
        });

        TenantContextHolder.setTenantId(tenant1);
        int tenant1AfterCreate = template.execute(status -> employeeService.findAll().size());
        assertThat(tenant1AfterCreate).isEqualTo(initialTenant1Count + 1);

        TenantContextHolder.setTenantId(tenant2);
        int tenant2Count = template.execute(status -> employeeService.findAll().size());
        assertThat(tenant2Count).isEqualTo(tenant1Employees.size());
    }
}
