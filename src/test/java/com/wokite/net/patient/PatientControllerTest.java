package com.wokite.net.patient;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.wokite.net.patient.domain.model.Sexe;
import com.wokite.net.patient.domain.model.TypeRelation;
import com.wokite.net.patient.web.PatientController;
import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.shared.security.JwtService;
import com.wokite.net.shared.web.error.BusinessException;
import com.wokite.net.shared.web.error.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PatientService patientService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void doitRechercherLesPatients() throws Exception {
        UUID patientUuid = UUID.randomUUID();

        PatientListeResponse response =
                new PatientListeResponse(
                        patientUuid,
                        "Diallo",
                        "Amadou",
                        LocalDate.of(2018, 5, 10),
                        Sexe.MASCULIN,
                        "Fatou Diallo",
                        "771234567",
                        RecordStatus.ACTIVE
                );

        when(patientService.rechercher(
                eq("diallo"),
                any()
        )).thenReturn(
                new PageImpl<>(
                        List.of(response),
                        PageRequest.of(0, 20),
                        1
                )
        );

        mockMvc.perform(
                        get("/patients")
                                .param("recherche", "diallo")
                                .param("page", "0")
                                .param("size", "20")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].uuid")
                        .value(patientUuid.toString()))
                .andExpect(jsonPath("$.content[0].nom")
                        .value("Diallo"))
                .andExpect(jsonPath("$.content[0].prenom")
                        .value("Amadou"))
                .andExpect(jsonPath("$.content[0].parentPrincipal")
                        .value("Fatou Diallo"));

        verify(patientService)
                .rechercher(eq("diallo"), any());
    }

    @Test
    void doitRetournerLeDetailDUnPatient() throws Exception {
        UUID patientUuid = UUID.randomUUID();
        UUID parentUuid = UUID.randomUUID();
        UUID relationUuid = UUID.randomUUID();

        ParentResponse parent =
                new ParentResponse(
                        parentUuid,
                        "Diallo",
                        "Fatou",
                        "771234567",
                        "fatou@example.com",
                        RecordStatus.ACTIVE
                );

        RelationParentPatientResponse relation =
                new RelationParentPatientResponse(
                        relationUuid,
                        parent,
                        TypeRelation.MERE,
                        RecordStatus.ACTIVE
                );

        PatientResponse response =
                new PatientResponse(
                        patientUuid,
                        "Diallo",
                        "Amadou",
                        LocalDate.of(2018, 5, 10),
                        Sexe.MASCULIN,
                        RecordStatus.ACTIVE,
                        List.of(relation)
                );

        when(patientService.getByUuid(patientUuid))
                .thenReturn(response);

        mockMvc.perform(
                        get("/patients/{patientUuid}", patientUuid)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid")
                        .value(patientUuid.toString()))
                .andExpect(jsonPath("$.nom")
                        .value("Diallo"))
                .andExpect(jsonPath("$.prenom")
                        .value("Amadou"))
                .andExpect(jsonPath("$.parents[0].uuid")
                        .value(relationUuid.toString()))
                .andExpect(jsonPath("$.parents[0].typeRelation")
                        .value("MERE"))
                .andExpect(jsonPath("$.parents[0].parent.uuid")
                        .value(parentUuid.toString()));

        verify(patientService)
                .getByUuid(patientUuid);
    }

    @Test
    void doitRetournerErreurSiPatientIntrouvable() throws Exception {
        UUID patientUuid = UUID.randomUUID();

        when(patientService.getByUuid(patientUuid))
                .thenThrow(
                        new BusinessException(
                                ErrorCode.RESOURCE_NOT_FOUND,
                                "Patient introuvable."
                        )
                );

        mockMvc.perform(
                        get("/patients/{patientUuid}", patientUuid)
                )
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code")
                        .value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message")
                        .value("Patient introuvable."));
    }

    @Test
    void doitCreerUnPatient() throws Exception {
        UUID patientUuid = UUID.randomUUID();

        CreerParentRequest parentRequest =
                new CreerParentRequest(
                        "Diallo",
                        "Fatou",
                        "771234567",
                        "fatou@example.com",
                        "Mariste"
                );

        CreerParentPatientRequest parentPatientRequest =
                new CreerParentPatientRequest(
                        parentRequest,
                        new RelationParentPatientRequest(TypeRelation.MERE)
                );

        CreerPatientRequest request =
                new CreerPatientRequest(
                        "Diallo",
                        "Amadou",
                        LocalDate.of(2018, 5, 10),
                        Sexe.MASCULIN,
                        List.of(parentPatientRequest)
                );

        PatientResponse response =
                new PatientResponse(
                        patientUuid,
                        "Diallo",
                        "Amadou",
                        LocalDate.of(2018, 5, 10),
                        Sexe.MASCULIN,
                        RecordStatus.ACTIVE,
                        List.of()
                );

        when(patientService.creer(any(CreerPatientRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid")
                        .value(patientUuid.toString()))
                .andExpect(jsonPath("$.nom")
                        .value("Diallo"))
                .andExpect(jsonPath("$.prenom")
                        .value("Amadou"));

        verify(patientService)
                .creer(any(CreerPatientRequest.class));
    }

    @Test
    void doitRefuserUneCreationAvecUnPatientInvalide() throws Exception {
        String request = """
                {
                    "nom": "",
                    "prenom": "",
                    "dateNaissance": null,
                    "sexe": null,
                    "parents": []
                }
                """;

        mockMvc.perform(
                        post("/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void doitModifierUnPatient() throws Exception {
        UUID patientUuid = UUID.randomUUID();

        ModifierPatientRequest request =
                new ModifierPatientRequest(
                        "Ba",
                        "Moussa",
                        LocalDate.of(2017, 8, 20),
                        Sexe.MASCULIN
                );

        PatientResponse response =
                new PatientResponse(
                        patientUuid,
                        "Ba",
                        "Moussa",
                        LocalDate.of(2017, 8, 20),
                        Sexe.MASCULIN,
                        RecordStatus.ACTIVE,
                        List.of()
                );

        when(patientService.modifier(
                eq(patientUuid),
                any(ModifierPatientRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/patients/{patientUuid}", patientUuid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid")
                        .value(patientUuid.toString()))
                .andExpect(jsonPath("$.nom")
                        .value("Ba"))
                .andExpect(jsonPath("$.prenom")
                        .value("Moussa"));

        verify(patientService)
                .modifier(
                        eq(patientUuid),
                        any(ModifierPatientRequest.class)
                );
    }

    @Test
    void doitSupprimerUnPatient() throws Exception {
        UUID patientUuid = UUID.randomUUID();

        doNothing()
                .when(patientService)
                .supprimer(patientUuid);

        mockMvc.perform(
                        delete("/patients/{patientUuid}", patientUuid)
                )
                .andExpect(status().isNoContent());

        verify(patientService)
                .supprimer(patientUuid);
    }

    @Test
    void doitRetournerLesParentsDUnPatient() throws Exception {
        UUID patientUuid = UUID.randomUUID();
        UUID parentUuid = UUID.randomUUID();

        ParentResponse parent =
                new ParentResponse(
                        parentUuid,
                        "Diallo",
                        "Fatou",
                        "771234567",
                        "fatou@example.com",
                        RecordStatus.ACTIVE
                );

        RelationParentPatientResponse relation =
                new RelationParentPatientResponse(
                        UUID.randomUUID(),
                        parent,
                        TypeRelation.MERE,
                        RecordStatus.ACTIVE
                );

        when(patientService.getParents(patientUuid))
                .thenReturn(List.of(relation));

        mockMvc.perform(
                        get("/patients/{patientUuid}/parents", patientUuid)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].parent.uuid")
                        .value(parentUuid.toString()))
                .andExpect(jsonPath("$[0].typeRelation")
                        .value("MERE"));

        verify(patientService)
                .getParents(patientUuid);
    }

    @Test
    void doitRetournerLesEnfantsDUnParent() throws Exception {
        UUID parentUuid = UUID.randomUUID();
        UUID patientUuid = UUID.randomUUID();

        PatientResponse response =
                new PatientResponse(
                        patientUuid,
                        "Diallo",
                        "Amadou",
                        LocalDate.of(2018, 5, 10),
                        Sexe.MASCULIN,
                        RecordStatus.ACTIVE,
                        List.of()
                );

        when(patientService.getEnfants(parentUuid))
                .thenReturn(List.of(response));

        mockMvc.perform(
                        get("/patients/{parentUuid}/enfants", parentUuid)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].uuid")
                        .value(patientUuid.toString()))
                .andExpect(jsonPath("$[0].nom")
                        .value("Diallo"));

        verify(patientService)
                .getEnfants(parentUuid);
    }

    @Test
    void doitModifierUnParent() throws Exception {
        UUID parentUuid = UUID.randomUUID();

        ModifierParentRequest request =
                new ModifierParentRequest(
                        "Ba",
                        "Awa",
                        "779876543",
                        "awa@example.com",
                        "Dakar"
                );

        ParentResponse response =
                new ParentResponse(
                        parentUuid,
                        "Ba",
                        "Awa",
                        "779876543",
                        "awa@example.com",
                        RecordStatus.ACTIVE
                );

        when(patientService.modifierParent(
                eq(parentUuid),
                any(ModifierParentRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/patients/parents/{parentUuid}", parentUuid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid")
                        .value(parentUuid.toString()))
                .andExpect(jsonPath("$.nom")
                        .value("Ba"))
                .andExpect(jsonPath("$.prenom")
                        .value("Awa"));

        verify(patientService)
                .modifierParent(
                        eq(parentUuid),
                        any(ModifierParentRequest.class)
                );
    }

    @Test
    void doitAjouterUnParentAUnPatient() throws Exception {
        UUID patientUuid = UUID.randomUUID();
        UUID parentUuid = UUID.randomUUID();
        UUID relationUuid = UUID.randomUUID();

        RelationParentPatientRequest request =
                new RelationParentPatientRequest(
                        TypeRelation.TUTRICE
                );

        ParentResponse parent =
                new ParentResponse(
                        parentUuid,
                        "Ba",
                        "Awa",
                        "779876543",
                        "awa@example.com",
                        RecordStatus.ACTIVE
                );

        RelationParentPatientResponse response =
                new RelationParentPatientResponse(
                        relationUuid,
                        parent,
                        TypeRelation.TUTRICE,
                        RecordStatus.ACTIVE
                );

        when(patientService.ajouterParentAuPatient(
                eq(patientUuid),
                eq(parentUuid),
                any(RelationParentPatientRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        post(
                                "/patients/{patientUuid}/parents/{parentUuid}",
                                patientUuid,
                                parentUuid
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid")
                        .value(relationUuid.toString()))
                .andExpect(jsonPath("$.parent.uuid")
                        .value(parentUuid.toString()))
                .andExpect(jsonPath("$.typeRelation")
                        .value("TUTRICE"));

        verify(patientService)
                .ajouterParentAuPatient(
                        eq(patientUuid),
                        eq(parentUuid),
                        any(RelationParentPatientRequest.class)
                );
    }

    @Test
    void doitSupprimerUneRelation() throws Exception {
        UUID relationUuid = UUID.randomUUID();

        doNothing()
                .when(patientService)
                .supprimerRelation(relationUuid);

        mockMvc.perform(
                        delete(
                                "/patients/relations/{relationUuid}",
                                relationUuid
                        )
                )
                .andExpect(status().isNoContent());

        verify(patientService)
                .supprimerRelation(relationUuid);
    }
}