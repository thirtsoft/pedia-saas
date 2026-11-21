package com.wokite.net.utilisateur.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AjouterPermissionRoleRequest(
        @NotNull(message = "La permission est obligatoire.") UUID permissionUuid) {
}