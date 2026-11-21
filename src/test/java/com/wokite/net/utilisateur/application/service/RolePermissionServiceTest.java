package com.wokite.net.utilisateur.application.service;

import com.wokite.net.utilisateur.application.dto.AjouterPermissionRoleRequest;
import com.wokite.net.utilisateur.domain.model.Permission;
import com.wokite.net.utilisateur.domain.model.Role;
import com.wokite.net.utilisateur.domain.model.RolePermission;
import com.wokite.net.utilisateur.domain.repository.PermissionRepository;
import com.wokite.net.utilisateur.domain.repository.RolePermissionRepository;
import com.wokite.net.utilisateur.domain.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolePermissionServiceTest {
    @Mock
    RoleRepository roles;
    @Mock
    PermissionRepository permissions;
    @Mock
    RolePermissionRepository links;
    RolePermissionService service;

    @BeforeEach
    void setUp() {
        service = new RolePermissionService(roles, permissions, links);
    }

    @Test
    void shouldLinkRolePermission() {
        UUID ru = UUID.randomUUID(), pu = UUID.randomUUID();
        Role r = new Role();
        r.setCode("MEDECIN");
        Permission p = new Permission();
        p.setCode("PATIENT_LIRE");
        p.setLibelle("Consulter");
        when(roles.findByUuid(ru)).thenReturn(java.util.Optional.of(r));
        when(permissions.findByUuid(pu)).thenReturn(java.util.Optional.of(p));
        when(links.existsByRoleUuidAndPermissionUuid(ru, pu)).thenReturn(false);
        RolePermission x = new RolePermission();
        x.setRole(r);
        x.setPermission(p);
        when(links.save(any(RolePermission.class))).thenReturn(x);
        var out = service.ajouter(ru, new AjouterPermissionRoleRequest(pu));
        assertThat(out.roleCode()).isEqualTo("MEDECIN");
        assertThat(out.permissionCode()).isEqualTo("PATIENT_LIRE");
    }
}