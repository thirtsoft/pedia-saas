package com.wokite.net.utilisateur.application.service;

import com.wokite.net.utilisateur.application.dto.CreerPermissionRequest;
import com.wokite.net.utilisateur.domain.model.Permission;
import com.wokite.net.utilisateur.domain.repository.PermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    PermissionRepository repo;
    PermissionService service;

    @BeforeEach
    void setUp() {
        service = new PermissionService(repo);
    }

    @Test
    void shouldCreatePermission() {
        when(repo.existsByCode("PATIENT_LIRE")).thenReturn(false);
        Permission p = new Permission();
        p.setCode("PATIENT_LIRE");
        p.setLibelle("Consulter les patients");
        when(repo.save(any(Permission.class))).thenReturn(p);
        var r = service.creer(new CreerPermissionRequest("PATIENT_LIRE", "Consulter les patients"));
        assertThat(r.code()).isEqualTo("PATIENT_LIRE");
    }
}