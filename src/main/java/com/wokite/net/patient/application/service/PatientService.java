package com.wokite.net.patient.application.service;

import com.wokite.net.patient.application.dto.CreerParentPatientRequest;
import com.wokite.net.patient.application.dto.CreerParentRequest;
import com.wokite.net.patient.application.dto.CreerPatientRequest;
import com.wokite.net.patient.application.dto.ModifierParentRequest;
import com.wokite.net.patient.application.dto.ModifierPatientRequest;
import com.wokite.net.patient.application.dto.ParentResponse;
import com.wokite.net.patient.application.dto.PatientListeResponse;
import com.wokite.net.patient.application.dto.PatientResponse;
import com.wokite.net.patient.application.dto.RelationParentPatientRequest;
import com.wokite.net.patient.application.dto.RelationParentPatientResponse;
import com.wokite.net.patient.domain.model.Parent;
import com.wokite.net.patient.domain.model.Patient;
import com.wokite.net.patient.domain.model.RelationParentPatient;
import com.wokite.net.patient.domain.repository.ParentRepository;
import com.wokite.net.patient.domain.repository.PatientRepository;
import com.wokite.net.patient.domain.repository.RelationParentPatientRepository;
import com.wokite.net.shared.web.error.BusinessException;
import com.wokite.net.shared.web.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final ParentRepository parentRepository;
    private final RelationParentPatientRepository relationRepository;

    public Page<PatientListeResponse> rechercher(String recherche, Pageable pageable) {

        Page<Patient> patients;

        if (recherche == null || recherche.isBlank()) {
            patients = patientRepository.findAll(pageable);
        } else {
            patients = patientRepository.rechercher(recherche.trim(), pageable);
        }

        return patients.map(this::toPatientListeResponse);
    }

    public PatientResponse getByUuid(UUID patientUuid) {
        Patient patient = getPatient(patientUuid);
        List<RelationParentPatientResponse> parents = relationRepository.findByPatientUuid(patientUuid)
                .stream()
                .map(this::toRelationResponse)
                .toList();

        return toPatientResponse(patient, parents);
    }

    @Transactional
    public PatientResponse creer(CreerPatientRequest request) {

        Patient patient = new Patient();

        patient.setNom(request.nom());
        patient.setPrenom(request.prenom());
        patient.setDateNaissance(request.dateNaissance());
        patient.setSexe(request.sexe());

        Patient patientSaved = patientRepository.saveAndFlush(patient);

        List<RelationParentPatientResponse> relations = new ArrayList<>();

        for (CreerParentPatientRequest parentPatientRequest : request.parents()) {

            Parent parent = creerParent(parentPatientRequest.parent());

            RelationParentPatient relation = new RelationParentPatient();

            relation.setPatientUuid(patientSaved.getUuid());
            relation.setParentUuid(parent.getUuid());
            relation.setTypeRelation(parentPatientRequest.relation().typeRelation());

            RelationParentPatient relationSaved = relationRepository.saveAndFlush(relation);

            relations.add(toRelationResponse(relationSaved));
        }

        return toPatientResponse(patientSaved, relations);
    }

    @Transactional
    public PatientResponse modifier(UUID patientUuid, ModifierPatientRequest request) {
        Patient patient = getPatient(patientUuid);

        patient.setNom(request.nom());
        patient.setPrenom(request.prenom());
        patient.setDateNaissance(request.dateNaissance());
        patient.setSexe(request.sexe());

        Patient saved = patientRepository.saveAndFlush(patient);

        List<RelationParentPatientResponse> parents = relationRepository.findByPatientUuid(saved.getUuid())
                .stream()
                .map(this::toRelationResponse)
                .toList();

        return toPatientResponse(saved, parents);
    }

    public ParentResponse modifierParent(UUID parentUuid, ModifierParentRequest request) {

        Parent parent = getParent(parentUuid);

        parent.setNom(request.nom());
        parent.setPrenom(request.prenom());
        parent.setTelephone(request.telephone());
        parent.setEmail(request.email());
        parent.setAdresse(request.adresse());

        return toParentResponse(parent);
    }

    @Transactional
    public RelationParentPatientResponse ajouterParentAuPatient(UUID patientUuid, UUID parentUuid, RelationParentPatientRequest request) {

        getPatient(patientUuid);
        getParent(parentUuid);

        verifierRelationInexistante(patientUuid, parentUuid);

        RelationParentPatient relation = new RelationParentPatient();
        relation.setPatientUuid(patientUuid);
        relation.setParentUuid(parentUuid);
        relation.setTypeRelation(request.typeRelation());
        RelationParentPatient saved = relationRepository.save(relation);

        return toRelationResponse(saved);
    }

    public List<RelationParentPatientResponse> getParents(UUID patientUuid) {
        getPatient(patientUuid);
        return relationRepository
                .findByPatientUuid(patientUuid)
                .stream()
                .map(this::toRelationResponse)
                .toList();
    }

    public List<PatientResponse> getEnfants(UUID parentUuid) {
        getParent(parentUuid);
        return relationRepository
                .findByParentUuid(parentUuid)
                .stream()
                .map(RelationParentPatient::getPatientUuid)
                .distinct()
                .map(this::getByUuid)
                .toList();
    }

    @Transactional
    public void supprimerRelation(UUID relationUuid) {
        RelationParentPatient relation = relationRepository.findByUuid(relationUuid).orElseThrow(() -> new BusinessException(
                ErrorCode.RESOURCE_NOT_FOUND,
                "Relation parent-patient introuvable."
        ));
        relationRepository.delete(relation);
    }

    @Transactional
    public void supprimer(UUID patientUuid) {
        Patient patient = getPatient(patientUuid);
        relationRepository.findByPatientUuid(patientUuid).forEach(relationRepository::delete);
        patientRepository.delete(patient);
    }

    private Parent creerParent(CreerParentRequest request) {
        Parent parent = new Parent();
        parent.setNom(request.nom());
        parent.setPrenom(request.prenom());
        parent.setTelephone(request.telephone());
        parent.setEmail(request.email());
        parent.setAdresse(request.adresse());
        return parentRepository.saveAndFlush(parent);
    }

    private void verifierRelationInexistante(
            UUID patientUuid,
            UUID parentUuid
    ) {

        if (relationRepository.existsByPatientUuidAndParentUuid(
                patientUuid,
                parentUuid
        )) {
            throw new BusinessException(
                    ErrorCode.CONFLICT,
                    "Ce parent est déjà associé à ce patient."
            );
        }
    }

    private Patient getPatient(UUID patientUuid) {

        return patientRepository.findByUuid(patientUuid)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Patient introuvable."
                ));
    }

    private Parent getParent(UUID parentUuid) {

        return parentRepository.findByUuid(parentUuid)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Parent introuvable."
                ));
    }

    private PatientListeResponse toPatientListeResponse(
            Patient patient
    ) {

        String parentPrincipal = null;
        String telephoneParentPrincipal = null;

        List<RelationParentPatient> relations =
                relationRepository.findByPatientUuid(
                        patient.getUuid()
                );

        if (!relations.isEmpty()) {

            RelationParentPatient relation =
                    relations.getFirst();

            Parent parent = parentRepository
                    .findByUuid(relation.getParentUuid())
                    .orElse(null);

            if (parent != null) {
                parentPrincipal =
                        parent.getPrenom() + " " + parent.getNom();

                telephoneParentPrincipal =
                        parent.getTelephone();
            }
        }

        return new PatientListeResponse(
                patient.getUuid(),
                patient.getNom(),
                patient.getPrenom(),
                patient.getDateNaissance(),
                patient.getSexe(),
                parentPrincipal,
                telephoneParentPrincipal,
                patient.getRecordStatus()
        );
    }

    private PatientResponse toPatientResponse(
            Patient patient,
            List<RelationParentPatientResponse> parents
    ) {

        return new PatientResponse(
                patient.getUuid(),
                patient.getNom(),
                patient.getPrenom(),
                patient.getDateNaissance(),
                patient.getSexe(),
                patient.getRecordStatus(),
                parents
        );
    }

    private RelationParentPatientResponse toRelationResponse(
            RelationParentPatient relation
    ) {

        Parent parent = getParent(
                relation.getParentUuid()
        );

        return new RelationParentPatientResponse(
                relation.getUuid(),
                toParentResponse(parent),
                relation.getTypeRelation(),
                relation.getRecordStatus()
        );
    }

    private ParentResponse toParentResponse(
            Parent parent
    ) {

        return new ParentResponse(
                parent.getUuid(),
                parent.getNom(),
                parent.getPrenom(),
                parent.getTelephone(),
                parent.getEmail(),
                parent.getRecordStatus()
        );
    }
}