package com.wokite.net.shared.persistence;

import com.wokite.net.shared.tenant.TenantContext;
import com.wokite.net.shared.tenant.TenantUuidResolver;
import com.wokite.net.shared.web.error.BusinessException;
import com.wokite.net.shared.web.error.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.util.UUID;

@Getter
@MappedSuperclass
@FilterDef(
        name = "tenantScope",
        autoEnabled = true,
        parameters = {
                @ParamDef(
                        name = "entrepriseUuid",
                        type = UUID.class,
                        resolver = TenantUuidResolver.class
                )
        }
)
@Filter(
        name = "tenantScope",
        condition = "entreprise_uuid = :entrepriseUuid"
)
public abstract class TenantAwareEntity extends BaseEntity {

    @Column(
            name = "entreprise_uuid",
            nullable = false,
            updatable = false
    )
    private UUID entrepriseUuid;

    @PrePersist
    protected void assignEntreprise() {

        UUID currentEntreprise = TenantContext.getTenantUuid();

        if (currentEntreprise == null) {
            throw new BusinessException(
                    ErrorCode.BUSINESS_ERROR,
                    "Impossible de créer l'entité : aucune entreprise courante."
            );
        }

        if (entrepriseUuid == null) {
            entrepriseUuid = currentEntreprise;
            return;
        }

        if (!entrepriseUuid.equals(currentEntreprise)) {
            throw new BusinessException(
                    ErrorCode.BUSINESS_ERROR,
                    "L'entreprise de l'entité ne correspond pas à l'entreprise courante."
            );
        }
    }
}