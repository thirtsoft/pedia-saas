package com.wokite.net.abonnement.application.service;

import com.wokite.net.abonnement.application.dto.AbonnementResponse;
import com.wokite.net.abonnement.application.dto.CreerAbonnementRequest;
import com.wokite.net.abonnement.application.dto.ModifierStatutAbonnementRequest;
import com.wokite.net.abonnement.domain.exception.AbonnementAlreadyActiveException;
import com.wokite.net.abonnement.domain.exception.AbonnementNotFoundException;
import com.wokite.net.abonnement.domain.exception.PlanAbonnementNotFoundException;
import com.wokite.net.abonnement.domain.model.Abonnement;
import com.wokite.net.abonnement.domain.model.PlanAbonnement;
import com.wokite.net.abonnement.domain.model.StatutAbonnement;
import com.wokite.net.abonnement.domain.repository.AbonnementRepository;
import com.wokite.net.abonnement.domain.repository.PlanAbonnementRepository;
import com.wokite.net.shared.persistence.RecordStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AbonnementService {

    private static final int DUREE_ESSAI_JOURS = 14;

    private static final List<StatutAbonnement> STATUTS_ACTIFS = List.of(
            StatutAbonnement.EN_PERIODE_ESSAI,
            StatutAbonnement.ACTIF,
            StatutAbonnement.SUSPENDU
    );

    private final AbonnementRepository abonnementRepository;
    private final PlanAbonnementRepository planRepository;

    public AbonnementService(AbonnementRepository abonnementRepository, PlanAbonnementRepository planRepository) {
        this.abonnementRepository = abonnementRepository;
        this.planRepository = planRepository;
    }

    @Transactional
    public AbonnementResponse creer(CreerAbonnementRequest request) {
        PlanAbonnement plan = planRepository.findByUuid(request.planUuid())
                .filter(PlanAbonnement::isActif)
                .orElseThrow(() -> new PlanAbonnementNotFoundException(request.planUuid()));

        if (abonnementRepository.existsByEntrepriseUuidAndStatutAbonnementIn(request.entrepriseUuid(), STATUTS_ACTIFS)) {
            throw new AbonnementAlreadyActiveException(request.entrepriseUuid());
        }

        Abonnement abonnement = new Abonnement();
        abonnement.setEntrepriseUuid(request.entrepriseUuid());
        abonnement.setPlanAbonnement(plan);
        abonnement.setDateDebut(request.dateDebut());
        abonnement.setDateFin(request.dateDebut().plusDays(DUREE_ESSAI_JOURS));
        abonnement.setStatutAbonnement(StatutAbonnement.EN_PERIODE_ESSAI);
        abonnement.setRecordStatus(RecordStatus.ACTIVE);

        Abonnement saved = abonnementRepository.save(abonnement);

        return toResponse(saved);
    }

    public AbonnementResponse rechercher(UUID uuid) {
        return toResponse(getEntity(uuid));
    }

    public AbonnementResponse rechercherActif(UUID entrepriseUuid) {
        return abonnementRepository.findFirstByEntrepriseUuidAndStatutAbonnementInOrderByDateDebutDesc(
                        entrepriseUuid, STATUTS_ACTIFS)
                .map(this::toResponse)
                .orElseThrow(() -> new AbonnementNotFoundException(entrepriseUuid));
    }

    public AbonnementResponse modifierStatut(UUID uuid, ModifierStatutAbonnementRequest request) {
        Abonnement abonnement = getEntity(uuid);
        abonnement.setStatutAbonnement(request.statut());
        return toResponse(abonnementRepository.save(abonnement));
    }

    private Abonnement getEntity(UUID uuid) {
        return abonnementRepository.findByUuid(uuid)
                .orElseThrow(() -> new AbonnementNotFoundException(uuid));
    }

    private AbonnementResponse toResponse(Abonnement abonnement) {
        return new AbonnementResponse(
                abonnement.getUuid(),
                abonnement.getEntrepriseUuid(),
                abonnement.getPlanAbonnement().getUuid(),
                abonnement.getDateDebut(),
                abonnement.getDateFin(),
                abonnement.getStatutAbonnement(),
                abonnement.getRecordStatus(),
                abonnement.getCreatedAt(),
                abonnement.getUpdatedAt()
        );
    }
}