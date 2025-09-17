package com.example.multitenant.config;

import com.example.multitenant.context.TenantContextHolder;
import com.example.multitenant.domain.Employee;
import com.example.multitenant.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedTenants(TenantDataSourceProperties tenantDataSourceProperties,
                                         EmployeeRepository employeeRepository,
                                         PlatformTransactionManager transactionManager) {
        return args -> {
            TransactionTemplate template = new TransactionTemplate(transactionManager);
            tenantDataSourceProperties.getDatasources().keySet().forEach(tenantId -> {
                TenantContextHolder.setTenantId(tenantId);
                try {
                    template.execute(status -> {
                        if (employeeRepository.count() == 0) {
                            employeeRepository.save(new Employee("John", "Doe", "Engineer"));
                            employeeRepository.save(new Employee("Jane", "Smith", "Manager"));
                        }
                        return null;
                    });
                } finally {
                    TenantContextHolder.clear();
                }
            });
        };
    }
}
