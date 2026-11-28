package com.wokite.net.abonnement.application.service;

import com.wokite.net.abonnement.application.dto.AbonnementResponse;
import com.wokite.net.abonnement.application.dto.CreerAbonnementRequest;
import com.wokite.net.abonnement.application.dto.ModifierStatutAbonnementRequest;
import com.wokite.net.abonnement.domain.exception.AbonnementAlreadyActiveException;
import com.wokite.net.abonnement.domain.exception.AbonnementNotFoundException;
import com.wokite.net.abonnement.domain.exception.PlanAbonnementAlreadyUsedException;
import com.wokite.net.abonnement.domain.exception.PlanAbonnementNotFoundException;
import com.wokite.net.abonnement.domain.model.Abonnement;
import com.wokite.net.abonnement.domain.model.PlanAbonnement;
import com.wokite.net.abonnement.domain.model.StatutAbonnement;
import com.wokite.net.abonnement.domain.repository.AbonnementRepository;
import com.wokite.net.abonnement.domain.repository.PlanAbonnementRepository;
import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.shared.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AbonnementService {

    private static final int DUREE_ESSAI_JOURS = 14;

    private static final List<StatutAbonnement> STATUTS_EN_COURS = List.of(
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

        UUID entrepriseUuid = getCurrentEntrepriseUuid();

        PlanAbonnement plan = planRepository.findByUuid(request.planUuid())
                .filter(PlanAbonnement::isActif).orElseThrow(() ->
                        new PlanAbonnementNotFoundException(request.planUuid())
                );

        if (abonnementRepository.existsByEntrepriseUuidAndStatutAbonnementIn(entrepriseUuid, STATUTS_EN_COURS)) {
            throw new AbonnementAlreadyActiveException(entrepriseUuid);
        }

        if (abonnementRepository.existsByEntrepriseUuidAndPlanAbonnementUuid(entrepriseUuid, request.planUuid())) {
            throw new PlanAbonnementAlreadyUsedException(
                    entrepriseUuid,
                    request.planUuid()
            );
        }

        Abonnement abonnement = new Abonnement();

        abonnement.setPlanAbonnement(plan);
        abonnement.setDateDebut(request.dateDebut());
        abonnement.setDateFin(request.dateDebut().plusDays(DUREE_ESSAI_JOURS));
        abonnement.setStatutAbonnement(StatutAbonnement.EN_PERIODE_ESSAI);
        abonnement.setRecordStatus(RecordStatus.ACTIVE);
        Abonnement saved = abonnementRepository.saveAndFlush(abonnement);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public AbonnementResponse rechercher(UUID uuid) {
        return toResponse(getEntity(uuid));
    }

    @Transactional(readOnly = true)
    public AbonnementResponse rechercherActif() {

        UUID entrepriseUuid = getCurrentEntrepriseUuid();

        return abonnementRepository.findFirstByEntrepriseUuidAndStatutAbonnementInOrderByDateDebutDesc(
                        entrepriseUuid, STATUTS_EN_COURS
                )
                .map(this::toResponse)
                .orElseThrow(() ->
                        new AbonnementNotFoundException(entrepriseUuid)
                );
    }

    @Transactional
    public AbonnementResponse modifierStatut(UUID uuid, ModifierStatutAbonnementRequest request) {
        Abonnement abonnement = getEntity(uuid);

        StatutAbonnement nouveauStatut = request.statut();

        if (STATUTS_EN_COURS.contains(nouveauStatut)
                && !STATUTS_EN_COURS.contains(abonnement.getStatutAbonnement())
                && abonnementRepository.existsByEntrepriseUuidAndStatutAbonnementIn(
                abonnement.getEntrepriseUuid(),
                STATUTS_EN_COURS
        )) {

            throw new AbonnementAlreadyActiveException(abonnement.getEntrepriseUuid());
        }

        abonnement.setStatutAbonnement(nouveauStatut);

        Abonnement saved = abonnementRepository.saveAndFlush(abonnement);

        return toResponse(saved);
    }

    private Abonnement getEntity(UUID uuid) {
        return abonnementRepository.findByUuid(uuid).orElseThrow(() ->
                new AbonnementNotFoundException(uuid)
        );
    }

    private UUID getCurrentEntrepriseUuid() {

        UUID entrepriseUuid = TenantContext.getTenantUuid();

        if (entrepriseUuid == null) {
            throw new IllegalStateException(
                    "Aucune entreprise courante dans le TenantContext."
            );
        }

        return entrepriseUuid;
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