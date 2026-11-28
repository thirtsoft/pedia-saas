package com.wokite.net.patient.application.dto;

import com.wokite.net.patient.domain.model.TypeRelation;
import jakarta.validation.constraints.NotNull;

public record RelationParentPatientRequest(

        @NotNull(message = "Le type de relation est obligatoire.")
        TypeRelation typeRelation
) {
}