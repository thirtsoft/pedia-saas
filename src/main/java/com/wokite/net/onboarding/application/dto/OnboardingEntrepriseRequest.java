package com.wokite.net.onboarding.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OnboardingEntrepriseRequest(

        @NotBlank(message = "Le nom du cabinet est obligatoire.")
        @Size(max = 150, message = "Le nom du cabinet ne doit pas dépasser 150 caractères.")
        String nom,

        @NotBlank(message = "Le téléphone professionnel est obligatoire.")
        @Size(max = 30, message = "Le téléphone ne doit pas dépasser 30 caractères.")
        String telephone,

        @Email(message = "L'adresse email professionnelle est invalide.")
        @Size(max = 180, message = "L'email ne doit pas dépasser 180 caractères.")
        String email,

        @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères.")
        String adresse

) {
}