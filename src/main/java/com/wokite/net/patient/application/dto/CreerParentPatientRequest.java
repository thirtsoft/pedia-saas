package com.wokite.net.patient.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CreerParentPatientRequest(

        @NotNull(message = "Les informations du parent sont obligatoires.")
        @Valid
        CreerParentRequest parent,

        @NotNull(message = "La relation avec le patient est obligatoire.")
        @Valid
        RelationParentPatientRequest relation
) {
}