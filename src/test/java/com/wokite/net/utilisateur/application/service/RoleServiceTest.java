package com.wokite.net.utilisateur.application.service;

import com.wokite.net.utilisateur.application.dto.CreerRoleRequest;
import com.wokite.net.utilisateur.domain.exception.RoleCodeAlreadyExistsException;
import com.wokite.net.utilisateur.domain.model.Role;
import com.wokite.net.utilisateur.domain.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    @Mock
    RoleRepository repo;
    RoleService service;

    @BeforeEach
    void setUp() {
        service = new RoleService(repo);
    }

    @Test
    void shouldCreateRole() {
        when(repo.existsByCode("MEDECIN")).thenReturn(false);
        Role saved = new Role();
        saved.setCode("MEDECIN");
        saved.setLibelle("Médecin");
        when(repo.save(any(Role.class))).thenReturn(saved);
        var r = service.creer(new CreerRoleRequest("medecin", "Médecin"));
        assertThat(r.code()).isEqualTo("MEDECIN");
        verify(repo).save(any(Role.class));
    }

    @Test
    void shouldRejectDuplicate() {
        when(repo.existsByCode("MEDECIN")).thenReturn(true);
        assertThatThrownBy(() -> service.creer(new CreerRoleRequest("MEDECIN", "Médecin"))).isInstanceOf(RoleCodeAlreadyExistsException.class);
        verify(repo, never()).save(any(Role.class));
    }
}