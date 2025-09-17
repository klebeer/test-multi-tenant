package com.example.multitenant.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    private final TenantConnectionRegistry registry;

    public TenantRoutingDataSource(TenantConnectionRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        TenantConnectionKey key = TenantContextHolder.getCurrent();
        registry.record(key);
        return key;
    }
}
