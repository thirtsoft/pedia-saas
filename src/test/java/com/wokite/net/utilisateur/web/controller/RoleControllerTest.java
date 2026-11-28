package com.wokite.net.utilisateur.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.shared.security.JwtService;
import com.wokite.net.shared.security.SecurityConfig;
import com.wokite.net.shared.web.error.GlobalExceptionHandler;
import com.wokite.net.utilisateur.application.dto.CreerRoleRequest;
import com.wokite.net.utilisateur.application.dto.RoleResponse;
import com.wokite.net.utilisateur.application.service.RoleService;
import com.wokite.net.utilisateur.domain.model.TypeRole;
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

@WebMvcTest(RoleController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class RoleControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    RoleService service;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser
    void shouldCreateRole() throws Exception {
        UUID u = UUID.randomUUID();
        when(service.creer(any(CreerRoleRequest.class))).thenReturn(new RoleResponse(u, "MEDECIN", "Médecin", TypeRole.MEDECIN_INDEPENDANT, RecordStatus.ACTIVE, null, null));
        mockMvc.perform(post("/roles").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new CreerRoleRequest("MEDECIN", "Médecin", TypeRole.MEDECIN_INDEPENDANT)))).andExpect(status().isCreated()).andExpect(jsonPath("$.code").value("MEDECIN"));
    }
}