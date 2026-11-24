package com.wokite.net.entreprise.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wokite.net.entreprise.application.dto.CreerEntrepriseRequest;
import com.wokite.net.entreprise.application.dto.EntrepriseResponse;
import com.wokite.net.entreprise.application.service.EntrepriseService;
import com.wokite.net.entreprise.domain.model.TypeEntreprise;
import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.shared.security.JwtService;
import com.wokite.net.shared.security.SecurityConfig;
import com.wokite.net.shared.web.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EntrepriseController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class EntrepriseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EntrepriseService entrepriseService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser
    void shouldCreateEntreprise() throws Exception {

        UUID uuid = UUID.randomUUID();

        CreerEntrepriseRequest request = new CreerEntrepriseRequest(
                "CABINET-DIALLO",
                "Cabinet Médical Diallo",
                TypeEntreprise.CABINET,
                "contact@cabinet.sn",
                "771234567",
                "Dakar"
        );

        EntrepriseResponse response = new EntrepriseResponse(
                uuid,
                "CABINET-DIALLO",
                "Cabinet Médical Diallo",
                TypeEntreprise.CABINET,
                "contact@cabinet.sn",
                "771234567",
                "Dakar",
                RecordStatus.ACTIVE,
                null,
                null
        );

        when(entrepriseService.creer(any(CreerEntrepriseRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/entreprises")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").value(uuid.toString()))
                .andExpect(jsonPath("$.code").value("CABINET-DIALLO"))
                .andExpect(jsonPath("$.nom")
                        .value("Cabinet Médical Diallo"))
                .andExpect(jsonPath("$.type").value("CABINET"))
                .andExpect(jsonPath("$.recordStatus")
                        .value("ACTIVE"));
    }

    @Test
    @WithMockUser
    void shouldRejectInvalidCreateRequest() throws Exception {

        CreerEntrepriseRequest request = new CreerEntrepriseRequest(
                "",
                "",
                null,
                "email-invalide",
                null,
                null
        );

        mockMvc.perform(
                        post("/entreprises")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @WithMockUser
    void shouldFindEntrepriseByUuid() throws Exception {

        UUID uuid = UUID.randomUUID();

        EntrepriseResponse response = new EntrepriseResponse(
                uuid,
                "MEDECIN-DIALLO",
                "Dr Diallo",
                TypeEntreprise.MEDECIN_INDEPENDANT,
                "diallo@example.com",
                "771234567",
                "Dakar",
                RecordStatus.ACTIVE,
                null,
                null
        );

        when(entrepriseService.rechercherParUuid(eq(uuid)))
                .thenReturn(response);

        mockMvc.perform(
                        get("/entreprises/{uuid}", uuid)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid")
                        .value(uuid.toString()))
                .andExpect(jsonPath("$.code")
                        .value("MEDECIN-DIALLO"))
                .andExpect(jsonPath("$.type")
                        .value("MEDECIN_INDEPENDANT"));
    }
}