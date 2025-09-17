package com.example.multitenant.config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class DataSourceConfiguration {

    private static final List<String> TENANTS = List.of("tenant1", "tenant2");

    @Bean
    public TenantConnectionRegistry tenantConnectionRegistry() {
        return new TenantConnectionRegistry();
    }

    @Bean
    @Primary
    public DataSource dataSource(TenantConnectionRegistry registry) {
        Map<Object, Object> dataSources = new HashMap<>();
        for (String tenant : TENANTS) {
            Map<DataSourceType, DataSource> perTenant = createTenantDataSources(tenant);
            for (Map.Entry<DataSourceType, DataSource> entry : perTenant.entrySet()) {
                dataSources.put(TenantConnectionKey.of(tenant, entry.getKey()), entry.getValue());
            }
        }
        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource(registry);
        routingDataSource.setTargetDataSources(dataSources);
        routingDataSource.setDefaultTargetDataSource(
                dataSources.get(TenantConnectionKey.of(TENANTS.get(0), DataSourceType.WRITE)));
        routingDataSource.setLenientFallback(false);
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }

    private Map<DataSourceType, DataSource> createTenantDataSources(String tenant) {
        Map<DataSourceType, DataSource> map = new EnumMap<>(DataSourceType.class);

        DataSource writeDataSource = createDataSource(tenant, DataSourceType.WRITE);
        initializeSchema(writeDataSource);
        map.put(DataSourceType.WRITE, writeDataSource);

        DataSource readDataSource = createDataSource(tenant, DataSourceType.READ);
        map.put(DataSourceType.READ, readDataSource);

        return map;
    }

    private DataSource createDataSource(String tenant, DataSourceType type) {
        String url = "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1;MODE=LEGACY".formatted(tenant);
        HikariDataSource dataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .driverClassName("org.h2.Driver")
                .url(url)
                .username("sa")
                .password("")
                .build();
        dataSource.setPoolName("%s-%s-pool".formatted(tenant, type.name().toLowerCase()));
        if (type == DataSourceType.READ) {
            dataSource.setReadOnly(true);
        }
        return dataSource;
    }

    private void initializeSchema(DataSource dataSource) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
        DatabasePopulatorUtils.execute(populator, dataSource);
    }
}
