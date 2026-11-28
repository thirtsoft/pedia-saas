package com.wokite.net.patient.application.dto;


import com.wokite.net.patient.domain.model.Sexe;
import com.wokite.net.shared.persistence.RecordStatus;

import java.time.LocalDate;
import java.util.UUID;

public record PatientListeResponse(

        UUID uuid,

        String nom,

        String prenom,

        LocalDate dateNaissance,

        Sexe sexe,

        String parentPrincipal,

        String telephoneParentPrincipal,

        RecordStatus recordStatus
) {
}