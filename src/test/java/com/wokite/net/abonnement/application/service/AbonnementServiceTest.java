package com.wokite.net.abonnement.application.service;

import com.wokite.net.abonnement.application.dto.CreerAbonnementRequest;
import com.wokite.net.abonnement.domain.exception.AbonnementAlreadyActiveException;
import com.wokite.net.abonnement.domain.model.Abonnement;
import com.wokite.net.abonnement.domain.model.PlanAbonnement;
import com.wokite.net.abonnement.domain.model.StatutAbonnement;
import com.wokite.net.abonnement.domain.repository.AbonnementRepository;
import com.wokite.net.abonnement.domain.repository.PlanAbonnementRepository;
import com.wokite.net.shared.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbonnementServiceTest {

    @Mock
    AbonnementRepository abonnementRepository;

    @Mock
    PlanAbonnementRepository planRepository;

    @InjectMocks
    AbonnementService service;

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldCreateFourteenDayTrial() {

        UUID entrepriseUuid = UUID.randomUUID();
        UUID planUuid = UUID.randomUUID();

        TenantContext.setTenantUuid(entrepriseUuid);

        PlanAbonnement plan = new PlanAbonnement();
        plan.setCode("PRO");
        plan.setNom("Plan Pro");
        plan.setPrixMensuel(new BigDecimal("15000"));
        plan.setNombreUtilisateursMax(10);
        plan.setActif(true);
        plan.setUuid(planUuid);

        when(planRepository.findByUuid(planUuid))
                .thenReturn(Optional.of(plan));

        when(abonnementRepository
                .existsByEntrepriseUuidAndStatutAbonnementIn(
                        eq(entrepriseUuid),
                        anyCollection()
                ))
                .thenReturn(false);

        when(abonnementRepository
                .existsByEntrepriseUuidAndPlanAbonnementUuid(
                        eq(entrepriseUuid),
                        eq(planUuid)
                ))
                .thenReturn(false);

        when(abonnementRepository.saveAndFlush(any(Abonnement.class)))
                .thenAnswer(invocation -> {
                    Abonnement abonnement = invocation.getArgument(0);
                    abonnement.setUuid(UUID.randomUUID());
                    return abonnement;
                });

        var response = service.creer(
                new CreerAbonnementRequest(
                        planUuid,
                        LocalDate.of(2026, 12, 1)
                )
        );

        assertThat(response.uuid())
                .isNotNull();

        assertThat(response.planUuid())
                .isEqualTo(planUuid);

        assertThat(response.dateDebut())
                .isEqualTo(LocalDate.of(2026, 12, 1));

        assertThat(response.dateFin())
                .isEqualTo(LocalDate.of(2026, 12, 15));

        assertThat(response.statut())
                .isEqualTo(StatutAbonnement.EN_PERIODE_ESSAI);
    }

    @Test
    void shouldRejectSecondActiveSubscription() {

        UUID entrepriseUuid = UUID.randomUUID();
        UUID planUuid = UUID.randomUUID();

        TenantContext.setTenantUuid(entrepriseUuid);

        PlanAbonnement plan = new PlanAbonnement();
        plan.setCode("PRO");
        plan.setNom("Plan Pro");
        plan.setPrixMensuel(new BigDecimal("15000"));
        plan.setNombreUtilisateursMax(10);
        plan.setActif(true);
        plan.setUuid(planUuid);

        when(planRepository.findByUuid(planUuid))
                .thenReturn(Optional.of(plan));

        when(abonnementRepository
                .existsByEntrepriseUuidAndStatutAbonnementIn(
                        eq(entrepriseUuid),
                        anyCollection()
                ))
                .thenReturn(true);

        assertThatThrownBy(() ->
                service.creer(
                        new CreerAbonnementRequest(
                                planUuid,
                                LocalDate.of(2026, 12, 1)
                        )
                )
        )
                .isInstanceOf(AbonnementAlreadyActiveException.class);
    }
}