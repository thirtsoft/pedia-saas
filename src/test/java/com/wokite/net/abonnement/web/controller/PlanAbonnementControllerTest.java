package com.wokite.net.abonnement.web.controller;

import com.wokite.net.abonnement.application.dto.PlanAbonnementResponse;
import com.wokite.net.abonnement.application.service.PlanAbonnementService;
import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.shared.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlanAbonnementController.class)
class PlanAbonnementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PlanAbonnementService service;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser
    void shouldGetActivePlans() throws Exception {
        when(service.listerActifs())
                .thenReturn(List.of(
                        new PlanAbonnementResponse(
                                UUID.randomUUID(),
                                "PRO",
                                "Plan Pro",
                                "Plan professionnel",
                                new BigDecimal("15000"),
                                10,
                                true,
                                RecordStatus.ACTIVE,
                                null,
                                null
                        )
                ));

        mockMvc.perform(get("/abonnement/plans/actifs"))
                .andExpect(status().isOk());
    }
}