package com.wokite.net.utilisateur.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.shared.security.SecurityConfig;
import com.wokite.net.shared.web.error.GlobalExceptionHandler;
import com.wokite.net.utilisateur.application.dto.CreerUtilisateurRequest;
import com.wokite.net.utilisateur.application.dto.UtilisateurResponse;
import com.wokite.net.utilisateur.application.service.UtilisateurService;
import com.wokite.net.utilisateur.domain.model.ProviderAuthentification;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UtilisateurController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class UtilisateurControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UtilisateurService service;

    @Test
    @WithMockUser
    void shouldCreateUtilisateur() throws Exception {
        UUID u = UUID.randomUUID(), eu = UUID.randomUUID(), ru = UUID.randomUUID();
        CreerUtilisateurRequest req = new CreerUtilisateurRequest(eu, ru, "Diallo", "Aminata", "a@b.sn", "password123", ProviderAuthentification.LOCAL);
        when(service.creer(any(CreerUtilisateurRequest.class))).thenReturn(new UtilisateurResponse(u, eu, ru, "SECRETAIRE", "Diallo", "Aminata", "a@b.sn", ProviderAuthentification.LOCAL, RecordStatus.ACTIVE, null, null));
        mockMvc.perform(post("/utilisateurs").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated()).andExpect(jsonPath("$.entrepriseUuid").value(eu.toString())).andExpect(jsonPath("$.roleCode").value("SECRETAIRE"));
    }
}