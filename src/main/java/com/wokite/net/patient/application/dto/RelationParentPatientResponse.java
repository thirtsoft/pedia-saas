package com.wokite.net.patient.application.dto;


import com.wokite.net.patient.domain.model.TypeRelation;
import com.wokite.net.shared.persistence.RecordStatus;

import java.util.UUID;

public record RelationParentPatientResponse(

        UUID uuid,

        ParentResponse parent,

        TypeRelation typeRelation,

        RecordStatus recordStatus
) {
}