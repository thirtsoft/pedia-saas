package com.wokite.net.abonnement.application.service;

import com.wokite.net.abonnement.application.dto.CreerAbonnementRequest;
import com.wokite.net.abonnement.domain.exception.AbonnementAlreadyActiveException;
import com.wokite.net.abonnement.domain.model.PlanAbonnement;
import com.wokite.net.abonnement.domain.model.StatutAbonnement;
import com.wokite.net.abonnement.domain.repository.AbonnementRepository;
import com.wokite.net.abonnement.domain.repository.PlanAbonnementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AbonnementServiceTest {

    @Mock
    AbonnementRepository abonnementRepository;

    @Mock
    PlanAbonnementRepository planRepository;

    @InjectMocks
    AbonnementService service;

    @Test
    void shouldCreateFourteenDayTrial() {
        UUID entrepriseUuid = UUID.randomUUID();
        UUID planUuid = UUID.randomUUID();

        PlanAbonnement plan = new PlanAbonnement();
        plan.setCode("PRO");
        plan.setNom("Plan Pro");
        plan.setPrixMensuel(new BigDecimal("15000"));
        plan.setNombreUtilisateursMax(10);
        plan.setActif(true);

        when(planRepository.findByUuid(planUuid))
                .thenReturn(Optional.of(plan));

        when(abonnementRepository
                .existsByEntrepriseUuidAndStatutAbonnementIn(
                        eq(entrepriseUuid),
                        anyCollection()
                ))
                .thenReturn(false);

        when(abonnementRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.creer(
                new CreerAbonnementRequest(
                        entrepriseUuid,
                        planUuid,
                        LocalDate.of(2026, 12, 1)
                )
        );

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

        PlanAbonnement plan = new PlanAbonnement();
        plan.setCode("PRO");
        plan.setNom("Plan Pro");
        plan.setPrixMensuel(new BigDecimal("15000"));
        plan.setNombreUtilisateursMax(10);
        plan.setActif(true);

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
                                entrepriseUuid,
                                planUuid,
                                LocalDate.of(2026, 12, 1)
                        )
                )
        )
                .isInstanceOf(AbonnementAlreadyActiveException.class);
    }
}