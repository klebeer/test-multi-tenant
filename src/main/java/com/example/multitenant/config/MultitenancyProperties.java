package com.example.multitenant.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "multitenancy")
public class MultitenancyProperties {

    private Map<String, TenantDataSourceProperties> tenants = new LinkedHashMap<>();

    public Map<String, TenantDataSourceProperties> getTenants() {
        return tenants;
    }

    public void setTenants(Map<String, TenantDataSourceProperties> tenants) {
        this.tenants = tenants;
    }

    public static class TenantDataSourceProperties {

        @NestedConfigurationProperty
        private DataSourceProperties write = new DataSourceProperties();

        @NestedConfigurationProperty
        private DataSourceProperties read = new DataSourceProperties();

        private boolean initializeSchema = true;

        public DataSourceProperties getWrite() {
            return write;
        }

        public void setWrite(DataSourceProperties write) {
            this.write = write;
        }

        public DataSourceProperties getRead() {
            return read;
        }

        public void setRead(DataSourceProperties read) {
            this.read = read;
        }

        public boolean isInitializeSchema() {
            return initializeSchema;
        }

        public void setInitializeSchema(boolean initializeSchema) {
            this.initializeSchema = initializeSchema;
        }
    }
}
