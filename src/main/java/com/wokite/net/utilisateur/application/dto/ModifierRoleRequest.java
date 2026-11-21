package com.wokite.net.utilisateur.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ModifierRoleRequest(

        @NotBlank(message = "Le libellé est obligatoire.")
        @Size(max = 100) String libelle) {
}