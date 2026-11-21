package com.wokite.net.utilisateur.application.service;

import com.wokite.net.utilisateur.application.dto.CreerUtilisateurRequest;
import com.wokite.net.utilisateur.domain.model.ProviderAuthentification;
import com.wokite.net.utilisateur.domain.model.Role;
import com.wokite.net.utilisateur.domain.model.Utilisateur;
import com.wokite.net.utilisateur.domain.repository.RoleRepository;
import com.wokite.net.utilisateur.domain.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {
    @Mock
    UtilisateurRepository users;
    @Mock
    RoleRepository roles;
    @Mock
    PasswordEncoder encoder;
    UtilisateurService service;

    @BeforeEach
    void setUp() {
        service = new UtilisateurService(users, roles, encoder);
    }

    @Test
    void shouldCreateUsingEntrepriseUuidNotRelation() {
        UUID eu = UUID.randomUUID(), ru = UUID.randomUUID();
        Role role = new Role();
        role.setCode("SECRETAIRE");
        when(users.existsByEmailIgnoreCase("a@b.sn")).thenReturn(false);
        when(roles.findByUuid(ru)).thenReturn(java.util.Optional.of(role));
        when(encoder.encode("password123")).thenReturn("encoded");
        Utilisateur u = new Utilisateur();
        u.setEntrepriseUuid(eu);
        u.setRole(role);
        u.setNom("Diallo");
        u.setPrenom("Aminata");
        u.setEmail("a@b.sn");
        u.setProviderAuthentification(ProviderAuthentification.LOCAL);
        when(users.save(any(Utilisateur.class))).thenReturn(u);
        var out = service.creer(new CreerUtilisateurRequest(eu, ru, "Diallo", "Aminata", "a@b.sn", "password123", ProviderAuthentification.LOCAL));
        assertThat(out.entrepriseUuid()).isEqualTo(eu);
        assertThat(out.roleCode()).isEqualTo("SECRETAIRE");
    }
}