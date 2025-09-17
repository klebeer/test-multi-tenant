package com.example.multitenant.config;

import com.example.multitenant.context.TenantContextHolder;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(TenantDataSourceProperties.class)
public class DataSourceConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                       DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.example.multitenant.domain")
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }

    @Bean
    @Primary
    public DataSource dataSource(TenantDataSourceProperties tenantDataSourceProperties) {
        Map<Object, Object> dataSources = new LinkedHashMap<>();
        tenantDataSourceProperties.getDatasources().forEach((tenantId, properties) -> {
            DataSourceProperties dsProps = properties.initializeDataSourceProperties();
            HikariDataSource hikari = dsProps.initializeDataSourceBuilder().type(HikariDataSource.class).build();
            hikari.setPoolName("tenant-" + tenantId);
            dataSources.put(tenantId, hikari);
        });
        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource();
        routingDataSource.setTargetDataSources(dataSources);
        routingDataSource.setDefaultTargetDataSource(dataSources.values().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No tenant data sources configured")));
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }

    static class TenantRoutingDataSource extends AbstractRoutingDataSource {
        @Override
        protected Object determineCurrentLookupKey() {
            return TenantContextHolder.getTenantId().orElse(null);
        }
    }
}
