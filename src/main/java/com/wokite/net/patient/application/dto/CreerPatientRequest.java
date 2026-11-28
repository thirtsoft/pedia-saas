package com.wokite.net.patient.application.dto;

import com.wokite.net.patient.domain.model.Sexe;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record CreerPatientRequest(

        @NotBlank(message = "Le nom du patient est obligatoire.")
        @Size(max = 100)
        String nom,

        @NotBlank(message = "Le prénom du patient est obligatoire.")
        @Size(max = 100)
        String prenom,

        @NotNull(message = "La date de naissance est obligatoire.")
        LocalDate dateNaissance,

        @NotNull(message = "Le sexe est obligatoire.")
        Sexe sexe,

        @NotEmpty(message = "Le patient doit avoir au moins un parent ou tuteur.")
        List<@Valid CreerParentPatientRequest> parents
) {
    public CreerPatientRequest {
        parents = parents == null
                ? List.of() : List.copyOf(parents);
    }
}