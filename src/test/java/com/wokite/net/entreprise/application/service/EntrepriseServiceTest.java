package com.wokite.net.entreprise.application.service;

import com.wokite.net.entreprise.application.dto.CreerEntrepriseRequest;
import com.wokite.net.entreprise.application.dto.EntrepriseResponse;
import com.wokite.net.entreprise.domain.exception.EntrepriseCodeAlreadyExistsException;
import com.wokite.net.entreprise.domain.model.Entreprise;
import com.wokite.net.entreprise.domain.model.TypeEntreprise;
import com.wokite.net.entreprise.domain.repository.EntrepriseRepository;
import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.shared.web.error.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntrepriseServiceTest {

    @Mock
    private EntrepriseRepository entrepriseRepository;

    private EntrepriseService entrepriseService;

    @BeforeEach
    void setUp() {
        entrepriseService = new EntrepriseService(entrepriseRepository);
    }

    @Test
    void shouldCreateEntreprise() {

        CreerEntrepriseRequest request = new CreerEntrepriseRequest(
                "cabinet-diallo",
                "Cabinet Médical Diallo",
                TypeEntreprise.CABINET,
                "contact@cabinet.sn",
                "771234567",
                "Dakar"
        );

        when(entrepriseRepository.existsByCodeIgnoreCase("CABINET-DIALLO"))
                .thenReturn(false);

        Entreprise saved = new Entreprise();
        saved.setCode("CABINET-DIALLO");
        saved.setNom("Cabinet Médical Diallo");
        saved.setType(TypeEntreprise.CABINET);
        saved.setEmail("contact@cabinet.sn");
        saved.setTelephone("771234567");
        saved.setAdresse("Dakar");
        saved.setRecordStatus(RecordStatus.ACTIVE);

        when(entrepriseRepository.save(any(Entreprise.class)))
                .thenReturn(saved);

        EntrepriseResponse response = entrepriseService.creer(request);

        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo("CABINET-DIALLO");
        assertThat(response.nom()).isEqualTo("Cabinet Médical Diallo");
        assertThat(response.type()).isEqualTo(TypeEntreprise.CABINET);
        assertThat(response.recordStatus()).isEqualTo(RecordStatus.ACTIVE);

        ArgumentCaptor<Entreprise> captor = ArgumentCaptor.forClass(Entreprise.class);

        verify(entrepriseRepository).save(captor.capture());

        Entreprise entity = captor.getValue();
        assertThat(entity.getCode()).isEqualTo("CABINET-DIALLO");
        assertThat(entity.getNom()).isEqualTo("Cabinet Médical Diallo");

        assertThat(entity.getType()).isEqualTo(TypeEntreprise.CABINET);
        assertThat(entity.getRecordStatus()).isEqualTo(RecordStatus.ACTIVE);
    }

    @Test
    void shouldRejectDuplicateEntrepriseCode() {
        CreerEntrepriseRequest request = new CreerEntrepriseRequest(
                "CABINET-DIALLO",
                "Cabinet Médical Diallo",
                TypeEntreprise.CABINET,
                null,
                null,
                null
        );

        when(entrepriseRepository.existsByCodeIgnoreCase("CABINET-DIALLO")).thenReturn(true);
        assertThatThrownBy(() -> entrepriseService.creer(request)
        )
                .isInstanceOf(EntrepriseCodeAlreadyExistsException.class)
                .hasMessageContaining("CABINET-DIALLO");

        verify(entrepriseRepository, never()).save(any(Entreprise.class));
    }

    @Test
    void shouldFindEntrepriseByUuid() {
        UUID uuid = UUID.randomUUID();
        Entreprise entreprise = new Entreprise();
        entreprise.setCode("CABINET-DIALLO");
        entreprise.setNom("Cabinet Médical Diallo");
        entreprise.setType(TypeEntreprise.CABINET);
        entreprise.setRecordStatus(RecordStatus.ACTIVE);

        when(entrepriseRepository.findByUuid(uuid)).thenReturn(java.util.Optional.of(entreprise));

        EntrepriseResponse response = entrepriseService.rechercherParUuid(uuid);
        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo("CABINET-DIALLO");
        assertThat(response.nom()).isEqualTo("Cabinet Médical Diallo");
    }

    @Test
    void shouldThrowNotFoundWhenEntrepriseDoesNotExist() {
        UUID uuid = UUID.randomUUID();
        when(entrepriseRepository.findByUuid(uuid)).thenReturn(java.util.Optional.empty());
        assertThatThrownBy(() -> entrepriseService.rechercherParUuid(uuid)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Entreprise introuvable.");
    }
}