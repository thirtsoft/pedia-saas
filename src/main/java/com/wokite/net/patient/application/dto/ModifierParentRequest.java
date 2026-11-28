package com.wokite.net.patient.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ModifierParentRequest(

        @NotBlank(message = "Le nom du parent est obligatoire.")
        @Size(max = 100)
        String nom,

        @NotBlank(message = "Le prénom du parent est obligatoire.")
        @Size(max = 100)
        String prenom,

        @NotBlank(message = "Le téléphone du parent est obligatoire.")
        @Size(max = 30)
        String telephone,

        @Email(message = "L'adresse email du parent est invalide.")
        @Size(max = 180)
        String email,

        @NotBlank(message = "Adresse du parent est obligatoire.")
        String adresse
) {
}