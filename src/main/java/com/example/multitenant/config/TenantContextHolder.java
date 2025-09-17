package com.example.multitenant.config;

public final class TenantContextHolder {

    private static final ThreadLocal<TenantContext> CONTEXT = new ThreadLocal<>();

    private TenantContextHolder() {
    }

    public static void setTenant(String tenantId) {
        TenantContext context = CONTEXT.get();
        if (context == null) {
            context = new TenantContext();
            CONTEXT.set(context);
        }
        context.setTenantId(tenantId);
    }

    public static void setDataSourceType(DataSourceType dataSourceType) {
        TenantContext context = CONTEXT.get();
        if (context == null) {
            context = new TenantContext();
            CONTEXT.set(context);
        }
        context.setDataSourceType(dataSourceType);
    }

    public static TenantConnectionKey getCurrent() {
        TenantContext context = CONTEXT.get();
        if (context == null || context.getTenantId() == null || context.getDataSourceType() == null) {
            return null;
        }
        return TenantConnectionKey.of(context.getTenantId(), context.getDataSourceType());
    }

    public static void clear() {
        CONTEXT.remove();
    }

    private static final class TenantContext {
        private String tenantId;
        private DataSourceType dataSourceType;

        private String getTenantId() {
            return tenantId;
        }

        private void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        private DataSourceType getDataSourceType() {
            return dataSourceType;
        }

        private void setDataSourceType(DataSourceType dataSourceType) {
            this.dataSourceType = dataSourceType;
        }
    }
}
