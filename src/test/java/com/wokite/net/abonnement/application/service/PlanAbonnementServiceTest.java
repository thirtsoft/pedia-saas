package com.wokite.net.abonnement.application.service;

import com.wokite.net.abonnement.application.dto.CreerPlanAbonnementRequest;
import com.wokite.net.abonnement.domain.exception.PlanAbonnementCodeAlreadyExistsException;
import com.wokite.net.abonnement.domain.model.PlanAbonnement;
import com.wokite.net.abonnement.domain.repository.PlanAbonnementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanAbonnementServiceTest {
    @Mock
    PlanAbonnementRepository repository;
    @InjectMocks
    PlanAbonnementService service;

    @Test
    void shouldCreatePlan() {
        var request = new CreerPlanAbonnementRequest(" pro ", "Plan Pro",
                "Plan professionnel", new BigDecimal("15000"), 10);
        when(repository.existsByCodeIgnoreCase("PRO")).thenReturn(false);
        when(repository.save(any(PlanAbonnement.class))).thenAnswer(i -> i.getArgument(0));

        var response = service.creer(request);

        assertThat(response.code()).isEqualTo("PRO");
        assertThat(response.prixMensuel()).isEqualByComparingTo("15000");
    }

    @Test
    void shouldRejectDuplicatePlanCode() {
        var request = new CreerPlanAbonnementRequest("PRO", "Plan Pro",
                null, new BigDecimal("15000"), 10);
        when(repository.existsByCodeIgnoreCase("PRO")).thenReturn(true);

        assertThatThrownBy(() -> service.creer(request))
                .isInstanceOf(PlanAbonnementCodeAlreadyExistsException.class);
    }
}