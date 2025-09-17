package com.example.multitenant.config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(MultitenancyProperties.class)
public class DataSourceConfiguration {

    private final MultitenancyProperties multitenancyProperties;

    public DataSourceConfiguration(MultitenancyProperties multitenancyProperties) {
        this.multitenancyProperties = multitenancyProperties;
    }

    @Bean
    public TenantConnectionRegistry tenantConnectionRegistry() {
        return new TenantConnectionRegistry();
    }

    @Bean
    @Primary
    public DataSource dataSource(TenantConnectionRegistry registry) {
        Map<String, MultitenancyProperties.TenantDataSourceProperties> tenants =
                multitenancyProperties.getTenants();
        Assert.state(!tenants.isEmpty(),
                "At least one tenant must be configured under 'multitenancy.tenants'.");

        Map<Object, Object> targetDataSources = new HashMap<>();
        DataSource defaultDataSource = null;

        for (Entry<String, MultitenancyProperties.TenantDataSourceProperties> entry : tenants.entrySet()) {
            String tenantId = entry.getKey();
            MultitenancyProperties.TenantDataSourceProperties tenantProperties = entry.getValue();

            Map<DataSourceType, DataSource> perTenantDataSources =
                    createTenantDataSources(tenantId, tenantProperties);
            perTenantDataSources.forEach((type, dataSource) ->
                    targetDataSources.put(TenantConnectionKey.of(tenantId, type), dataSource));

            if (defaultDataSource == null) {
                defaultDataSource = perTenantDataSources.get(DataSourceType.WRITE);
            }
        }

        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource(registry);
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(defaultDataSource);
        routingDataSource.setLenientFallback(false);
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    private Map<DataSourceType, DataSource> createTenantDataSources(
            String tenantId, MultitenancyProperties.TenantDataSourceProperties properties) {
        Map<DataSourceType, DataSource> map = new EnumMap<>(DataSourceType.class);

        DataSource writeDataSource = buildDataSource(tenantId, DataSourceType.WRITE, properties.getWrite(), false);
        if (properties.isInitializeSchema()) {
            initializeSchema(writeDataSource);
        }
        map.put(DataSourceType.WRITE, writeDataSource);

        DataSource readDataSource;
        if (isConfigured(properties.getRead())) {
            readDataSource = buildDataSource(tenantId, DataSourceType.READ, properties.getRead(), true);
        } else {
            readDataSource = writeDataSource;
        }
        map.put(DataSourceType.READ, readDataSource);

        return map;
    }

    private DataSource buildDataSource(String tenantId, DataSourceType type, DataSourceProperties properties,
            boolean readOnly) {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        dataSource.setPoolName("%s-%s-pool".formatted(tenantId, type.name().toLowerCase()));
        dataSource.setReadOnly(readOnly);
        return dataSource;
    }

    private boolean isConfigured(DataSourceProperties properties) {
        if (properties == null) {
            return false;
        }
        return StringUtils.hasText(properties.getUrl())
                || StringUtils.hasText(properties.getJndiName())
                || StringUtils.hasText(properties.getDriverClassName())
                || StringUtils.hasText(properties.getUsername());
    }

    private void initializeSchema(DataSource dataSource) {
        ResourceDatabasePopulator populator =
                new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
        DatabasePopulatorUtils.execute(populator, dataSource);
    }
}
