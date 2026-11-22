package com.wokite.net.utilisateur.auth;


import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.utilisateur.application.dto.InscriptionResponse;
import com.wokite.net.utilisateur.application.dto.InscrireUtilisateurRequest;
import com.wokite.net.utilisateur.domain.exception.UtilisateurEmailAlreadyExistsException;
import com.wokite.net.utilisateur.domain.model.ProviderAuthentification;
import com.wokite.net.utilisateur.domain.model.Utilisateur;
import com.wokite.net.utilisateur.domain.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthentificationService {

    private final UtilisateurRepository users;
    private final PasswordEncoder encoder;

    public AuthentificationService(UtilisateurRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    public InscriptionResponse inscrire(InscrireUtilisateurRequest request) {
        String email = normaliserEmail(request.email());

        if (users.existsByEmailIgnoreCase(email)) {
            throw new UtilisateurEmailAlreadyExistsException(email);
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.nom().trim());
        utilisateur.setPrenom(request.prenom().trim());
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(encoder.encode(request.motDePasse()));
        utilisateur.setProviderAuthentification(ProviderAuthentification.LOCAL);
        utilisateur.setEntrepriseUuid(null);
        utilisateur.setRole(null);
        utilisateur.setRecordStatus(RecordStatus.ACTIVE);
        Utilisateur saved = users.save(utilisateur);

        return toResponse(saved);
    }

    private String normaliserEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private InscriptionResponse toResponse(Utilisateur utilisateur) {
        return new InscriptionResponse(
                utilisateur.getUuid(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getProviderAuthentification(),
                utilisateur.getEntrepriseUuid() == null
                        || utilisateur.getRole() == null,
                utilisateur.getRecordStatus(),
                utilisateur.getCreatedAt()
        );
    }
}