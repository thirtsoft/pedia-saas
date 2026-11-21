package com.wokite.net.entreprise.application.dto;

import com.wokite.net.entreprise.domain.model.TypeEntreprise;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreerEntrepriseRequest(

        @NotBlank(message = "Le code est obligatoire.")
        @Size(
                min = 2,
                max = 50,
                message = "Le code doit contenir entre 2 et 50 caractères."
        )
        String code,

        @NotBlank(message = "Le nom est obligatoire.")
        @Size(
                min = 2,
                max = 150,
                message = "Le nom doit contenir entre 2 et 150 caractères."
        )
        String nom,

        @NotNull(message = "Le type d'entreprise est obligatoire.")
        TypeEntreprise type,

        @Email(message = "L'adresse email est invalide.")
        @Size(max = 180, message = "L'email est trop long.")
        String email,

        @Size(max = 30, message = "Le numéro de téléphone est trop long.")
        String telephone,

        @Size(max = 255, message = "L'adresse est trop longue.")
        String adresse
) {
}