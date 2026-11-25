package com.wokite.net.shared.tenant;

import java.util.UUID;

public final class TenantContext {

    private static final ThreadLocal<UUID> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setTenantUuid(UUID tenantUuid) {
        CURRENT_TENANT.set(tenantUuid);
    }

    public static UUID getTenantUuid() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}