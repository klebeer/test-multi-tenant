package com.example.multitenant.config;

import java.util.Objects;

public final class TenantConnectionKey {

    private final String tenantId;
    private final DataSourceType dataSourceType;

    private TenantConnectionKey(String tenantId, DataSourceType dataSourceType) {
        this.tenantId = tenantId;
        this.dataSourceType = dataSourceType;
    }

    public static TenantConnectionKey of(String tenantId, DataSourceType dataSourceType) {
        return new TenantConnectionKey(tenantId, dataSourceType);
    }

    public String tenantId() {
        return tenantId;
    }

    public DataSourceType dataSourceType() {
        return dataSourceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TenantConnectionKey that = (TenantConnectionKey) o;
        return Objects.equals(tenantId, that.tenantId) && dataSourceType == that.dataSourceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, dataSourceType);
    }

    @Override
    public String toString() {
        return tenantId + ":" + dataSourceType;
    }
}
