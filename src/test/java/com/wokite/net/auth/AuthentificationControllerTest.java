package com.wokite.net.auth;

import com.wokite.net.shared.security.SecurityConfig;
import com.wokite.net.shared.web.error.GlobalExceptionHandler;
import com.wokite.net.utilisateur.application.dto.InscriptionResponse;
import com.wokite.net.utilisateur.application.dto.InscrireUtilisateurRequest;
import com.wokite.net.utilisateur.auth.AuthentificationController;
import com.wokite.net.utilisateur.auth.AuthentificationService;
import com.wokite.net.utilisateur.domain.model.ProviderAuthentification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthentificationController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class AuthentificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthentificationService service;

    @Test
    void registerDoitEtreAccessibleSansAuthentification() throws Exception {
        InscriptionResponse response = new InscriptionResponse(
                null,
                "Diallo",
                "Tairou",
                "tairou@example.com",
                ProviderAuthentification.LOCAL,
                true,
                com.wokite.net.shared.persistence.RecordStatus.ACTIVE,
                Instant.now()
        );

        when(service.inscrire(any(InscrireUtilisateurRequest.class))).thenReturn(response);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nom": "Diallo",
                                  "prenom": "Tairou",
                                  "email": "tairou@example.com",
                                  "motDePasse": "Password123"
                                }
                                """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Diallo"))
                .andExpect(jsonPath("$.prenom").value("Tairou"))
                .andExpect(jsonPath("$.email").value("tairou@example.com"))
                .andExpect(jsonPath("$.providerAuthentification").value("LOCAL"))
                .andExpect(jsonPath("$.onboardingRequis").value(true));
    }

    @Test
    void registerDoitRefuserUneRequeteInvalide() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nom": "",
                                  "prenom": "",
                                  "email": "email-invalide",
                                  "motDePasse": "123"
                                }
                                """)
                )
                .andExpect(status().isBadRequest());
    }
}