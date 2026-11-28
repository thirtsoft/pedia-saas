package com.wokite.net.patient.application.dto;


import com.wokite.net.patient.domain.model.Sexe;
import com.wokite.net.shared.persistence.RecordStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PatientResponse(

        UUID uuid,

        String nom,

        String prenom,

        LocalDate dateNaissance,

        Sexe sexe,

        RecordStatus recordStatus,

        List<RelationParentPatientResponse> parents

) {

    public PatientResponse {
        parents = parents == null
                ? List.of() : List.copyOf(parents);
    }
}