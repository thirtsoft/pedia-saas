package com.wokite.net.utilisateur.application.dto;

import com.wokite.net.utilisateur.domain.model.TypeRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreerRoleRequest(

        @NotBlank(message = "Le code est obligatoire.") @Size(max = 50)
        String code,

        @NotBlank(message = "Le libellé est obligatoire.") @Size(max = 100)
        String libelle,

        @NotNull(message = "Le type de rôle est obligatoire.")
        TypeRole typeRole
) {
}