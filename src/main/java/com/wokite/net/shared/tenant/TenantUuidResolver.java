package com.wokite.net.shared.tenant;

import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Supplier;

@Component
public class TenantUuidResolver implements Supplier<UUID> {

    @Override
    public UUID get() {

        UUID tenantUuid = TenantContext.getTenantUuid();

        if (tenantUuid == null) {
            throw new IllegalStateException(
                    "TenantContext non initialisé pour la requête courante."
            );
        }

        return tenantUuid;
    }
}