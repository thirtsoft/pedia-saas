package com.wokite.net.patient.application.dto;

import com.wokite.net.patient.domain.model.Sexe;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ModifierPatientRequest(

        @NotBlank(message = "Le nom du patient est obligatoire.")
        @Size(max = 100)
        String nom,

        @NotBlank(message = "Le prénom du patient est obligatoire.")
        @Size(max = 100)
        String prenom,

        @NotNull(message = "La date de naissance est obligatoire.")
        LocalDate dateNaissance,

        @NotNull(message = "Le sexe est obligatoire.")
        Sexe sexe
) {
}