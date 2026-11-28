package com.wokite.net.patient.application.dto;


import com.wokite.net.shared.persistence.RecordStatus;

import java.util.UUID;

public record ParentResponse(
        UUID uuid,
        String nom,
        String prenom,
        String telephone,
        String email,
        RecordStatus recordStatus
) {
}