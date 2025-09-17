package com.example.multitenant.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "multitenancy")
public class TenantDataSourceProperties {

    private Map<String, TenantDataSource> datasources = new LinkedHashMap<>();

    public Map<String, TenantDataSource> getDatasources() {
        return datasources;
    }

    public void setDatasources(Map<String, TenantDataSource> datasources) {
        this.datasources = datasources;
    }

    public static class TenantDataSource {
        private String url;
        private String username;
        private String password;
        private String driverClassName;

        public DataSourceProperties initializeDataSourceProperties() {
            DataSourceProperties properties = new DataSourceProperties();
            properties.setUrl(url);
            properties.setUsername(username);
            properties.setPassword(password);
            properties.setDriverClassName(driverClassName);
            return properties;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }
    }
}
