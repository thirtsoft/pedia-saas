package com.wokite.net.patient.entite;

import com.wokite.net.patient.enumeration.TypeRelationPatientParent;

public class PatientParent {

    @Enumerated(EnumType.STRING)
    @Column(name = "type_relation", nullable = false, length = 30)
    private TypeRelationPatientParent typeRelationPatientParent;
}