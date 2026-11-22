package com.wokite.net.abonnement.application.service;

import com.wokite.net.abonnement.application.dto.CreerFactureAbonnementRequest;
import com.wokite.net.abonnement.application.dto.FactureAbonnementResponse;
import com.wokite.net.abonnement.application.dto.ModifierStatutFactureAbonnementRequest;
import com.wokite.net.abonnement.domain.exception.AbonnementNotFoundException;
import com.wokite.net.abonnement.domain.exception.FactureAbonnementNotFoundException;
import com.wokite.net.abonnement.domain.exception.FactureAbonnementNumeroAlreadyExistsException;
import com.wokite.net.abonnement.domain.model.Abonnement;
import com.wokite.net.abonnement.domain.model.FactureAbonnement;
import com.wokite.net.abonnement.domain.model.StatutFactureAbonnement;
import com.wokite.net.abonnement.domain.repository.AbonnementRepository;
import com.wokite.net.abonnement.domain.repository.FactureAbonnementRepository;
import com.wokite.net.shared.persistence.RecordStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class FactureAbonnementService {

    private static final DateTimeFormatter YEAR = DateTimeFormatter.ofPattern("yyyy");

    private final FactureAbonnementRepository factureRepository;
    private final AbonnementRepository abonnementRepository;

    public FactureAbonnementService(FactureAbonnementRepository factureRepository, AbonnementRepository abonnementRepository) {
        this.factureRepository = factureRepository;
        this.abonnementRepository = abonnementRepository;
    }

    @Transactional
    public FactureAbonnementResponse creer(CreerFactureAbonnementRequest request) {
        Abonnement abonnement = abonnementRepository.findByUuid(request.abonnementUuid())
                .orElseThrow(() -> new AbonnementNotFoundException(request.abonnementUuid()));
        if (!abonnement.getEntrepriseUuid().equals(request.entrepriseUuid())) {
            throw new IllegalArgumentException(
                    "L'entreprise de la facture ne correspond pas "
                            + "à l'entreprise de l'abonnement."
            );
        }

        String numero = request.numero();

        if (numero == null || numero.isBlank()) {
            numero = genererNumero();
        } else {
            numero = numero.trim().toUpperCase();
        }

        if (factureRepository.existsByNumero(numero)) {
            throw new FactureAbonnementNumeroAlreadyExistsException(numero);
        }

        FactureAbonnement facture = getFactureAbonnement(request, abonnement, numero);

        FactureAbonnement saved = factureRepository.save(facture);
        return toResponse(saved);
    }

    private static FactureAbonnement getFactureAbonnement(CreerFactureAbonnementRequest request, Abonnement abonnement, String numero) {
        FactureAbonnement facture = new FactureAbonnement();
        facture.setEntrepriseUuid(request.entrepriseUuid());
        facture.setAbonnement(abonnement);
        facture.setNumero(numero);
        facture.setDateEmission(request.dateEmission());
        facture.setDateEcheance(request.dateEcheance());
        facture.setMontant(request.montant());
        facture.setStatutFactureAbonnement(StatutFactureAbonnement.EN_ATTENTE);
        facture.setRecordStatus(RecordStatus.ACTIVE);
        return facture;
    }

    public FactureAbonnementResponse rechercher(UUID uuid) {
        return toResponse(factureRepository.findByUuid(uuid)
                .orElseThrow(() ->
                        new FactureAbonnementNotFoundException(uuid))
        );
    }

    public List<FactureAbonnementResponse> listerParEntreprise(UUID entrepriseUuid) {
        return factureRepository.findAllByEntrepriseUuidOrderByDateEmissionDesc(entrepriseUuid)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FactureAbonnementResponse modifierStatut(UUID uuid, ModifierStatutFactureAbonnementRequest request) {
        FactureAbonnement facture = factureRepository.findByUuid(uuid).orElseThrow(() ->
                new FactureAbonnementNotFoundException(uuid));
        facture.setStatutFactureAbonnement(request.statut());
        return toResponse(factureRepository.save(facture));
    }

    private String genererNumero() {
        String prefix = "FAC-" + LocalDate.now().format(YEAR) + "-";
        long n = factureRepository.count() + 1L;
        String numero = prefix + String.format("%06d", n);
        while (factureRepository.existsByNumero(numero)) {
            n++;
            numero = prefix + String.format("%06d", n);
        }
        return numero;
    }

    private FactureAbonnementResponse toResponse(
            FactureAbonnement facture) {

        return new FactureAbonnementResponse(
                facture.getUuid(),
                facture.getEntrepriseUuid(),
                facture.getAbonnement().getUuid(),
                facture.getNumero(),
                facture.getDateEmission(),
                facture.getDateEcheance(),
                facture.getMontant(),
                facture.getStatutFactureAbonnement(),
                facture.getRecordStatus(),
                facture.getCreatedAt(),
                facture.getUpdatedAt()
        );
    }
}