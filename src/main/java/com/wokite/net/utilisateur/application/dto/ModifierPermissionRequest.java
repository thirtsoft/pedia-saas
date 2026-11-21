package com.wokite.net.utilisateur.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ModifierPermissionRequest(

        @NotBlank(message = "Le libellé est obligatoire.")
        @Size(max = 150) String libelle) {
}