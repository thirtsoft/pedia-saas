package com.wokite.net.patient.web;

import com.wokite.net.patient.application.dto.CreerPatientRequest;
import com.wokite.net.patient.application.dto.ModifierParentRequest;
import com.wokite.net.patient.application.dto.ModifierPatientRequest;
import com.wokite.net.patient.application.dto.ParentResponse;
import com.wokite.net.patient.application.dto.PatientListeResponse;
import com.wokite.net.patient.application.dto.PatientResponse;
import com.wokite.net.patient.application.dto.RelationParentPatientRequest;
import com.wokite.net.patient.application.dto.RelationParentPatientResponse;
import com.wokite.net.patient.application.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<Page<PatientListeResponse>> rechercher(
            @RequestParam(required = false) String recherche,
            @PageableDefault(size = 20, sort = "nom")
            Pageable pageable
    ) {
        return ResponseEntity.ok(patientService.rechercher(recherche, pageable));
    }

    @GetMapping("/{patientUuid}")
    public ResponseEntity<PatientResponse> getByUuid(@PathVariable UUID patientUuid) {
        return ResponseEntity.ok(patientService.getByUuid(patientUuid));
    }

    @PostMapping
    public ResponseEntity<PatientResponse> creer(@Valid @RequestBody CreerPatientRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(patientService.creer(request));
    }

    @PutMapping("/{patientUuid}")
    public ResponseEntity<PatientResponse> modifier(@PathVariable UUID patientUuid, @Valid @RequestBody ModifierPatientRequest request) {
        return ResponseEntity.ok(
                patientService.modifier(patientUuid, request));
    }

    @DeleteMapping("/{patientUuid}")
    public ResponseEntity<Void> supprimer(@PathVariable UUID patientUuid) {
        patientService.supprimer(patientUuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{patientUuid}/parents")
    public ResponseEntity<List<RelationParentPatientResponse>> getParents(@PathVariable UUID patientUuid) {
        return ResponseEntity.ok(patientService.getParents(patientUuid));
    }

    @GetMapping("/{parentUuid}/enfants")
    public ResponseEntity<List<PatientResponse>> getEnfants(@PathVariable UUID parentUuid) {
        return ResponseEntity.ok(patientService.getEnfants(parentUuid));
    }

    @PutMapping("/parents/{parentUuid}")
    public ResponseEntity<ParentResponse> modifierParent(@PathVariable UUID parentUuid, @Valid @RequestBody ModifierParentRequest request) {
        return ResponseEntity.ok(
                patientService.modifierParent(parentUuid, request));
    }

    @PostMapping("/{patientUuid}/parents/{parentUuid}")
    public ResponseEntity<RelationParentPatientResponse> ajouterParentAuPatient(
            @PathVariable UUID patientUuid,
            @PathVariable UUID parentUuid,
            @Valid @RequestBody RelationParentPatientRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        patientService.ajouterParentAuPatient(
                                patientUuid,
                                parentUuid,
                                request
                        )
                );
    }

    @DeleteMapping("/relations/{relationUuid}")
    public ResponseEntity<Void> supprimerRelation(@PathVariable UUID relationUuid) {
        patientService.supprimerRelation(relationUuid);
        return ResponseEntity.noContent().build();
    }
}