package com.wokite.net.utilisateur.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreerPermissionRequest(

        @NotBlank(message = "Le code est obligatoire.")
        @Size(max = 100) String code,

        @NotBlank(message = "Le libellé est obligatoire.") @Size(max = 150)
        String libelle) {
}