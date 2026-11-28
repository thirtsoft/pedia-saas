package com.wokite.net.patient;

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
import com.wokite.net.patient.application.service.PatientService;
import com.wokite.net.patient.domain.model.Parent;
import com.wokite.net.patient.domain.model.Patient;
import com.wokite.net.patient.domain.model.RelationParentPatient;
import com.wokite.net.patient.domain.model.Sexe;
import com.wokite.net.patient.domain.model.TypeRelation;
import com.wokite.net.patient.domain.repository.ParentRepository;
import com.wokite.net.patient.domain.repository.PatientRepository;
import com.wokite.net.patient.domain.repository.RelationParentPatientRepository;
import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.shared.web.error.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private ParentRepository parentRepository;

    @Mock
    private RelationParentPatientRepository relationRepository;

    @InjectMocks
    private PatientService patientService;

    private UUID patientUuid;
    private UUID parentUuid;
    private UUID relationUuid;

    @BeforeEach
    void setUp() {
        patientUuid = UUID.randomUUID();
        parentUuid = UUID.randomUUID();
        relationUuid = UUID.randomUUID();
    }

    @Test
    void doitRetournerUnePageDePatients() {
        Patient patient = patient(
                patientUuid,
                "Diallo",
                "Amadou",
                LocalDate.of(2018, 5, 10),
                Sexe.MASCULIN
        );

        Pageable pageable = PageRequest.of(0, 20);

        when(patientRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(patient), pageable, 1));

        when(relationRepository.findByPatientUuid(patientUuid))
                .thenReturn(List.of());

        Page<PatientListeResponse> result =
                patientService.rechercher(null, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).uuid())
                .isEqualTo(patientUuid);
        assertThat(result.getContent().get(0).nom())
                .isEqualTo("Diallo");
        assertThat(result.getContent().get(0).prenom())
                .isEqualTo("Amadou");

        verify(patientRepository).findAll(pageable);
        verify(patientRepository, never())
                .rechercher(any(), any());
    }

    @Test
    void doitRechercherUnPatientAvecUnTexte() {
        Patient patient = patient(
                patientUuid,
                "Diallo",
                "Amadou",
                LocalDate.of(2018, 5, 10),
                Sexe.MASCULIN
        );

        Pageable pageable = PageRequest.of(0, 20);

        when(patientRepository.rechercher("diallo", pageable))
                .thenReturn(new PageImpl<>(List.of(patient), pageable, 1));

        when(relationRepository.findByPatientUuid(patientUuid))
                .thenReturn(List.of());

        Page<PatientListeResponse> result =
                patientService.rechercher("  diallo  ", pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).nom())
                .isEqualTo("Diallo");

        verify(patientRepository)
                .rechercher("diallo", pageable);

        verify(patientRepository, never())
                .findAll(any(Pageable.class));
    }

    @Test
    void doitRetournerLeDetailDUnPatientAvecSesParents() {
        Patient patient = patient(
                patientUuid,
                "Diallo",
                "Amadou",
                LocalDate.of(2018, 5, 10),
                Sexe.MASCULIN
        );

        Parent parent = parent(
                parentUuid,
                "Diallo",
                "Fatou",
                "771234567"
        );

        RelationParentPatient relation = relation(
                relationUuid,
                parentUuid,
                patientUuid,
                TypeRelation.MERE
        );

        when(patientRepository.findByUuid(patientUuid))
                .thenReturn(Optional.of(patient));

        when(relationRepository.findByPatientUuid(patientUuid))
                .thenReturn(List.of(relation));

        when(parentRepository.findByUuid(parentUuid))
                .thenReturn(Optional.of(parent));

        PatientResponse result =
                patientService.getByUuid(patientUuid);

        assertThat(result).isNotNull();
        assertThat(result.uuid()).isEqualTo(patientUuid);
        assertThat(result.nom()).isEqualTo("Diallo");
        assertThat(result.prenom()).isEqualTo("Amadou");

        assertThat(result.parents())
                .hasSize(1);

        RelationParentPatientResponse parentResponse =
                result.parents().get(0);

        assertThat(parentResponse.uuid())
                .isEqualTo(relationUuid);

        assertThat(parentResponse.typeRelation())
                .isEqualTo(TypeRelation.MERE);

        assertThat(parentResponse.parent().uuid())
                .isEqualTo(parentUuid);

        assertThat(parentResponse.parent().prenom())
                .isEqualTo("Fatou");
    }

    @Test
    void doitLeverUneErreurSiPatientIntrouvable() {
        when(patientRepository.findByUuid(patientUuid))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                patientService.getByUuid(patientUuid)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage("Patient introuvable.");
    }

    @Test
    void doitCreerUnPatientAvecUnParent() {
        CreerParentRequest parentRequest =
                new CreerParentRequest(
                        "Diallo",
                        "Fatou",
                        "771234567",
                        "fatou@example.com",
                        "Malick Sy Almadies"
                );

        RelationParentPatientRequest relationRequest =
                new RelationParentPatientRequest(TypeRelation.MERE);

        CreerParentPatientRequest parentPatientRequest =
                new CreerParentPatientRequest(
                        parentRequest,
                        relationRequest
                );

        CreerPatientRequest request =
                new CreerPatientRequest(
                        "Diallo",
                        "Amadou",
                        LocalDate.of(2018, 5, 10),
                        Sexe.MASCULIN,
                        List.of(parentPatientRequest)
                );

        Patient patient = patient(
                patientUuid,
                "Diallo",
                "Amadou",
                LocalDate.of(2018, 5, 10),
                Sexe.MASCULIN
        );

        Parent parent = parent(
                parentUuid,
                "Diallo",
                "Fatou",
                "771234567"
        );

        RelationParentPatient relation = relation(
                relationUuid,
                parentUuid,
                patientUuid,
                TypeRelation.MERE
        );

        when(patientRepository.save(any(Patient.class)))
                .thenReturn(patient);

        when(parentRepository.save(any(Parent.class)))
                .thenReturn(parent);

        when(relationRepository.save(any(RelationParentPatient.class)))
                .thenReturn(relation);

        when(parentRepository.findByUuid(parentUuid))
                .thenReturn(Optional.of(parent));

        PatientResponse result =
                patientService.creer(request);

        assertThat(result).isNotNull();
        assertThat(result.uuid()).isEqualTo(patientUuid);
        assertThat(result.nom()).isEqualTo("Diallo");
        assertThat(result.prenom()).isEqualTo("Amadou");

        assertThat(result.parents())
                .hasSize(1);

        assertThat(result.parents().get(0).typeRelation())
                .isEqualTo(TypeRelation.MERE);

        verify(patientRepository)
                .save(any(Patient.class));

        verify(parentRepository)
                .save(any(Parent.class));

        verify(relationRepository)
                .save(any(RelationParentPatient.class));
    }

    @Test
    void doitCreerUnPatientAvecPlusieursParents() {
        UUID parentUuid1 = UUID.randomUUID();
        UUID parentUuid2 = UUID.randomUUID();

        Parent parent1 = parent(
                parentUuid1,
                "Diallo",
                "Mamadou",
                "771111111"
        );

        Parent parent2 = parent(
                parentUuid2,
                "Ba",
                "Awa",
                "772222222"
        );

        RelationParentPatient relation1 = relation(
                UUID.randomUUID(),
                parentUuid1,
                patientUuid,
                TypeRelation.PERE
        );

        RelationParentPatient relation2 = relation(
                UUID.randomUUID(),
                parentUuid2,
                patientUuid,
                TypeRelation.MERE
        );

        when(patientRepository.save(any(Patient.class)))
                .thenReturn(
                        patient(
                                patientUuid,
                                "Diallo",
                                "Ibrahima",
                                LocalDate.of(2019, 2, 15),
                                Sexe.MASCULIN
                        )
                );

        when(parentRepository.save(any(Parent.class)))
                .thenReturn(parent1)
                .thenReturn(parent2);

        when(relationRepository.save(any(RelationParentPatient.class)))
                .thenReturn(relation1)
                .thenReturn(relation2);

        when(parentRepository.findByUuid(parentUuid1))
                .thenReturn(Optional.of(parent1));

        when(parentRepository.findByUuid(parentUuid2))
                .thenReturn(Optional.of(parent2));

        CreerParentPatientRequest parentRequest1 =
                new CreerParentPatientRequest(
                        new CreerParentRequest(
                                "Diallo",
                                "Mamadou",
                                "771111111",
                                null,
                                null
                        ),
                        new RelationParentPatientRequest(
                                TypeRelation.PERE
                        )
                );

        CreerParentPatientRequest parentRequest2 =
                new CreerParentPatientRequest(
                        new CreerParentRequest(
                                "Ba",
                                "Awa",
                                "772222222",
                                null,
                                null
                        ),
                        new RelationParentPatientRequest(
                                TypeRelation.MERE
                        )
                );

        CreerPatientRequest request =
                new CreerPatientRequest(
                        "Diallo",
                        "Ibrahima",
                        LocalDate.of(2019, 2, 15),
                        Sexe.MASCULIN,
                        List.of(parentRequest1, parentRequest2)
                );

        PatientResponse result =
                patientService.creer(request);

        assertThat(result.parents())
                .hasSize(2);

        assertThat(result.parents())
                .extracting(RelationParentPatientResponse::typeRelation)
                .containsExactly(
                        TypeRelation.PERE,
                        TypeRelation.MERE
                );

        verify(parentRepository, times(2))
                .save(any(Parent.class));

        verify(relationRepository, times(2))
                .save(any(RelationParentPatient.class));
    }

    @Test
    void doitModifierUnPatient() {
        Patient patient = patient(
                patientUuid,
                "Diallo",
                "Amadou",
                LocalDate.of(2018, 5, 10),
                Sexe.MASCULIN
        );

        when(patientRepository.findByUuid(patientUuid))
                .thenReturn(Optional.of(patient));

        when(relationRepository.findByPatientUuid(patientUuid))
                .thenReturn(List.of());

        ModifierPatientRequest request =
                new ModifierPatientRequest(
                        "Ba",
                        "Moussa",
                        LocalDate.of(2017, 8, 20),
                        Sexe.MASCULIN
                );

        PatientResponse result =
                patientService.modifier(patientUuid, request);

        assertThat(result.nom())
                .isEqualTo("Ba");

        assertThat(result.prenom())
                .isEqualTo("Moussa");

        assertThat(result.dateNaissance())
                .isEqualTo(LocalDate.of(2017, 8, 20));

        assertThat(result.sexe())
                .isEqualTo(Sexe.MASCULIN);

        verify(patientRepository)
                .findByUuid(patientUuid);
    }

    @Test
    void doitModifierUnParent() {
        Parent parent = parent(
                parentUuid,
                "Diallo",
                "Fatou",
                "771234567"
        );

        when(parentRepository.findByUuid(parentUuid))
                .thenReturn(Optional.of(parent));

        ModifierParentRequest request =
                new ModifierParentRequest(
                        "Ba",
                        "Awa",
                        "779876543",
                        "awa@example.com",
                        "Dakar"
                );

        ParentResponse result =
                patientService.modifierParent(parentUuid, request);

        assertThat(result.uuid())
                .isEqualTo(parentUuid);

        assertThat(result.nom())
                .isEqualTo("Ba");

        assertThat(result.prenom())
                .isEqualTo("Awa");

        assertThat(result.telephone())
                .isEqualTo("779876543");

        assertThat(result.email())
                .isEqualTo("awa@example.com");

        verify(parentRepository)
                .findByUuid(parentUuid);
    }

    @Test
    void doitAjouterUnParentExistantAUnPatient() {
        Patient patient = patient(
                patientUuid,
                "Diallo",
                "Amadou",
                LocalDate.of(2018, 5, 10),
                Sexe.MASCULIN
        );

        Parent parent = parent(
                parentUuid,
                "Diallo",
                "Fatou",
                "771234567"
        );

        RelationParentPatient relation = relation(
                relationUuid,
                parentUuid,
                patientUuid,
                TypeRelation.TUTRICE
        );

        when(patientRepository.findByUuid(patientUuid))
                .thenReturn(Optional.of(patient));

        when(parentRepository.findByUuid(parentUuid))
                .thenReturn(Optional.of(parent));

        when(relationRepository.existsByPatientUuidAndParentUuid(
                patientUuid,
                parentUuid
        )).thenReturn(false);

        when(relationRepository.save(any(RelationParentPatient.class)))
                .thenReturn(relation);

        when(parentRepository.findByUuid(parentUuid))
                .thenReturn(Optional.of(parent));

        RelationParentPatientResponse result =
                patientService.ajouterParentAuPatient(
                        patientUuid,
                        parentUuid,
                        new RelationParentPatientRequest(
                                TypeRelation.TUTRICE
                        )
                );

        assertThat(result.uuid())
                .isEqualTo(relationUuid);

        assertThat(result.typeRelation())
                .isEqualTo(TypeRelation.TUTRICE);

        assertThat(result.parent().uuid())
                .isEqualTo(parentUuid);

        ArgumentCaptor<RelationParentPatient> captor =
                ArgumentCaptor.forClass(RelationParentPatient.class);

        verify(relationRepository)
                .save(captor.capture());

        assertThat(captor.getValue().getPatientUuid())
                .isEqualTo(patientUuid);

        assertThat(captor.getValue().getParentUuid())
                .isEqualTo(parentUuid);

        assertThat(captor.getValue().getTypeRelation())
                .isEqualTo(TypeRelation.TUTRICE);
    }

    @Test
    void neDoitPasAjouterDeuxFoisLeMemeParentAuMemePatient() {
        when(patientRepository.findByUuid(patientUuid))
                .thenReturn(Optional.of(
                        patient(
                                patientUuid,
                                "Diallo",
                                "Amadou",
                                LocalDate.of(2018, 5, 10),
                                Sexe.MASCULIN
                        )
                ));

        when(parentRepository.findByUuid(parentUuid))
                .thenReturn(Optional.of(
                        parent(
                                parentUuid,
                                "Diallo",
                                "Fatou",
                                "771234567"
                        )
                ));

        when(relationRepository.existsByPatientUuidAndParentUuid(
                patientUuid,
                parentUuid
        )).thenReturn(true);

        assertThatThrownBy(() ->
                patientService.ajouterParentAuPatient(
                        patientUuid,
                        parentUuid,
                        new RelationParentPatientRequest(
                                TypeRelation.MERE
                        )
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage(
                        "Ce parent est déjà associé à ce patient."
                );

        verify(relationRepository, never())
                .save(any(RelationParentPatient.class));
    }

    @Test
    void doitRetournerLesParentsDUnPatient() {
        Parent parent = parent(
                parentUuid,
                "Diallo",
                "Fatou",
                "771234567"
        );

        RelationParentPatient relation = relation(
                relationUuid,
                parentUuid,
                patientUuid,
                TypeRelation.MERE
        );

        when(patientRepository.findByUuid(patientUuid))
                .thenReturn(Optional.of(
                        patient(
                                patientUuid,
                                "Diallo",
                                "Amadou",
                                LocalDate.of(2018, 5, 10),
                                Sexe.MASCULIN
                        )
                ));

        when(relationRepository.findByPatientUuid(patientUuid))
                .thenReturn(List.of(relation));

        when(parentRepository.findByUuid(parentUuid))
                .thenReturn(Optional.of(parent));

        List<RelationParentPatientResponse> result =
                patientService.getParents(patientUuid);

        assertThat(result)
                .hasSize(1);

        assertThat(result.get(0).parent().uuid())
                .isEqualTo(parentUuid);

        assertThat(result.get(0).typeRelation())
                .isEqualTo(TypeRelation.MERE);
    }

    @Test
    void doitRetournerLesEnfantsDUnParent() {
        UUID patientUuid2 = UUID.randomUUID();

        Patient enfant1 = patient(
                patientUuid,
                "Diallo",
                "Amadou",
                LocalDate.of(2018, 5, 10),
                Sexe.MASCULIN
        );

        Patient enfant2 = patient(
                patientUuid2,
                "Diallo",
                "Awa",
                LocalDate.of(2020, 3, 15),
                Sexe.FEMININ
        );

        RelationParentPatient relation1 = relation(
                UUID.randomUUID(),
                parentUuid,
                patientUuid,
                TypeRelation.PERE
        );

        RelationParentPatient relation2 = relation(
                UUID.randomUUID(),
                parentUuid,
                patientUuid2,
                TypeRelation.TUTEUR
        );

        when(parentRepository.findByUuid(parentUuid))
                .thenReturn(Optional.of(
                        parent(
                                parentUuid,
                                "Diallo",
                                "Mamadou",
                                "771234567"
                        )
                ));

        when(relationRepository.findByParentUuid(parentUuid))
                .thenReturn(List.of(relation1, relation2));

        when(patientRepository.findByUuid(patientUuid))
                .thenReturn(Optional.of(enfant1));

        when(patientRepository.findByUuid(patientUuid2))
                .thenReturn(Optional.of(enfant2));

        when(relationRepository.findByPatientUuid(patientUuid))
                .thenReturn(List.of(relation1));

        when(relationRepository.findByPatientUuid(patientUuid2))
                .thenReturn(List.of(relation2));

        when(parentRepository.findByUuid(parentUuid))
                .thenReturn(Optional.of(
                        parent(
                                parentUuid,
                                "Diallo",
                                "Mamadou",
                                "771234567"
                        )
                ));

        List<PatientResponse> result =
                patientService.getEnfants(parentUuid);

        assertThat(result)
                .hasSize(2);

        assertThat(result)
                .extracting(PatientResponse::uuid)
                .containsExactly(
                        patientUuid,
                        patientUuid2
                );
    }

    @Test
    void doitSupprimerUneRelation() {
        RelationParentPatient relation =
                relation(
                        relationUuid,
                        parentUuid,
                        patientUuid,
                        TypeRelation.MERE
                );

        when(relationRepository.findByUuid(relationUuid))
                .thenReturn(Optional.of(relation));

        patientService.supprimerRelation(relationUuid);

        verify(relationRepository)
                .delete(relation);
    }

    @Test
    void doitLeverUneErreurLorsquOnSupprimeUneRelationIntrouvable() {
        when(relationRepository.findByUuid(relationUuid))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                patientService.supprimerRelation(relationUuid)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage("Relation parent-patient introuvable.");

        verify(relationRepository, never())
                .delete(any(RelationParentPatient.class));
    }

    @Test
    void doitSupprimerUnPatientEtSesRelations() {
        RelationParentPatient relation =
                relation(
                        relationUuid,
                        parentUuid,
                        patientUuid,
                        TypeRelation.MERE
                );

        Patient patient = patient(
                patientUuid,
                "Diallo",
                "Amadou",
                LocalDate.of(2018, 5, 10),
                Sexe.MASCULIN
        );

        when(patientRepository.findByUuid(patientUuid))
                .thenReturn(Optional.of(patient));

        when(relationRepository.findByPatientUuid(patientUuid))
                .thenReturn(List.of(relation));

        patientService.supprimer(patientUuid);

        verify(relationRepository)
                .delete(relation);

        verify(patientRepository)
                .delete(patient);
    }

    private Patient patient(
            UUID uuid,
            String nom,
            String prenom,
            LocalDate dateNaissance,
            Sexe sexe
    ) {
        Patient patient = new Patient();
        patient.setUuid(uuid);
        patient.setNom(nom);
        patient.setPrenom(prenom);
        patient.setDateNaissance(dateNaissance);
        patient.setSexe(sexe);
        patient.setRecordStatus(RecordStatus.ACTIVE);
        return patient;
    }

    private Parent parent(
            UUID uuid,
            String nom,
            String prenom,
            String telephone
    ) {
        Parent parent = new Parent();
        parent.setUuid(uuid);
        parent.setNom(nom);
        parent.setPrenom(prenom);
        parent.setTelephone(telephone);
        parent.setRecordStatus(RecordStatus.ACTIVE);
        return parent;
    }

    private RelationParentPatient relation(
            UUID uuid,
            UUID parentUuid,
            UUID patientUuid,
            TypeRelation typeRelation
    ) {
        RelationParentPatient relation =
                new RelationParentPatient();

        relation.setUuid(uuid);
        relation.setParentUuid(parentUuid);
        relation.setPatientUuid(patientUuid);
        relation.setTypeRelation(typeRelation);
        relation.setRecordStatus(RecordStatus.ACTIVE);

        return relation;
    }
}