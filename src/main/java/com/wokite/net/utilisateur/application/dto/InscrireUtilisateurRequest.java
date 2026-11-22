package com.wokite.net.utilisateur.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InscrireUtilisateurRequest(

        @NotBlank(message = "Le nom est obligatoire.")
        @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères.")
        String nom,

        @NotBlank(message = "Le prénom est obligatoire.")
        @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères.")
        String prenom,

        @NotBlank(message = "L'adresse email est obligatoire.")
        @Email(message = "L'adresse email est invalide.")
        @Size(max = 180, message = "L'adresse email ne doit pas dépasser 180 caractères.")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire.")
        @Size(min = 8, max = 255, message = "Le mot de passe doit contenir entre 8 et 255 caractères.")
        String motDePasse
) {
}