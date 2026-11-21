package com.wokite.net.utilisateur.application.dto;

import java.util.UUID;

public record RolePermissionResponse(
        UUID roleUuid,
        String roleCode,
        UUID permissionUuid,
        String permissionCode,
        String permissionLibelle) {
}