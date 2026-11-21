package com.wokite.net.entreprise.application.service;

import com.wokite.net.entreprise.application.dto.CreerEntrepriseRequest;
import com.wokite.net.entreprise.application.dto.EntrepriseResponse;
import com.wokite.net.entreprise.domain.exception.EntrepriseCodeAlreadyExistsException;
import com.wokite.net.entreprise.domain.model.Entreprise;
import com.wokite.net.entreprise.domain.repository.EntrepriseRepository;
import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.shared.web.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class EntrepriseService {

    private final EntrepriseRepository entrepriseRepository;

    public EntrepriseService(EntrepriseRepository entrepriseRepository) {
        this.entrepriseRepository = entrepriseRepository;
    }

    @Transactional
    public EntrepriseResponse creer(CreerEntrepriseRequest request) {

        String code = normaliserCode(request.code());

        if (entrepriseRepository.existsByCodeIgnoreCase(code)) {
            throw new EntrepriseCodeAlreadyExistsException(code);
        }

        Entreprise entreprise = new Entreprise();
        entreprise.setCode(code);
        entreprise.setNom(request.nom().trim());
        entreprise.setType(request.type());
        entreprise.setEmail(normaliserNullable(request.email()));
        entreprise.setTelephone(normaliserNullable(request.telephone()));
        entreprise.setAdresse(normaliserNullable(request.adresse()));
        entreprise.setRecordStatus(RecordStatus.ACTIVE);

        Entreprise saved = entrepriseRepository.save(entreprise);

        return toResponse(saved);
    }

    public EntrepriseResponse rechercherParUuid(UUID uuid) {
        Entreprise entreprise = entrepriseRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Entreprise introuvable."
                ));

        return toResponse(entreprise);
    }

    private EntrepriseResponse toResponse(Entreprise entreprise) {
        return new EntrepriseResponse(
                entreprise.getUuid(),
                entreprise.getCode(),
                entreprise.getNom(),
                entreprise.getType(),
                entreprise.getEmail(),
                entreprise.getTelephone(),
                entreprise.getAdresse(),
                entreprise.getRecordStatus(),
                entreprise.getCreatedAt(),
                entreprise.getUpdatedAt()
        );
    }

    private String normaliserCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private String normaliserNullable(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isBlank() ? null : normalized;
    }


}