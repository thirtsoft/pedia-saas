package com.wokite.net.utilisateur.application.service;

import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.shared.web.error.ResourceNotFoundException;
import com.wokite.net.utilisateur.application.dto.CreerUtilisateurRequest;
import com.wokite.net.utilisateur.application.dto.ModifierUtilisateurRequest;
import com.wokite.net.utilisateur.application.dto.UtilisateurResponse;
import com.wokite.net.utilisateur.domain.exception.MotDePasseObligatoireException;
import com.wokite.net.utilisateur.domain.exception.UtilisateurEmailAlreadyExistsException;
import com.wokite.net.utilisateur.domain.model.ProviderAuthentification;
import com.wokite.net.utilisateur.domain.model.Role;
import com.wokite.net.utilisateur.domain.model.Utilisateur;
import com.wokite.net.utilisateur.domain.repository.RoleRepository;
import com.wokite.net.utilisateur.domain.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
public class UtilisateurService {

    private final UtilisateurRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;

    public UtilisateurService(UtilisateurRepository users, RoleRepository roles, PasswordEncoder encoder) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
    }

    public UtilisateurResponse creer(CreerUtilisateurRequest r) {
        String email = norm(r.email());
        if (users.existsByEmailIgnoreCase(email)) {
            throw new UtilisateurEmailAlreadyExistsException(email);
        }
        Role role = findRole(r.roleUuid());
        ProviderAuthentification p = r.providerAuthentification() == null ? ProviderAuthentification.LOCAL : r.providerAuthentification();
        if (p == ProviderAuthentification.LOCAL && r.motDePasse() == null) {
            throw new MotDePasseObligatoireException();
        }
        Utilisateur u = new Utilisateur();
        u.setEntrepriseUuid(r.entrepriseUuid());
        u.setRole(role);
        u.setNom(r.nom().trim());
        u.setPrenom(r.prenom().trim());
        u.setEmail(email);
        u.setProviderAuthentification(p);
        u.setRecordStatus(RecordStatus.ACTIVE);
        if (p == ProviderAuthentification.LOCAL) {
            u.setMotDePasse(encoder.encode(r.motDePasse()));
        }
        return toResponse(users.save(u));
    }

    public UtilisateurResponse modifier(UUID id, ModifierUtilisateurRequest r) {
        Utilisateur u = find(id);
        String email = norm(r.email());
        if (users.existsByEmailIgnoreCaseAndUuidNot(email, id)) {
            throw new UtilisateurEmailAlreadyExistsException(email);
        }
        u.setRole(findRole(r.roleUuid()));
        u.setNom(r.nom().trim());
        u.setPrenom(r.prenom().trim());
        u.setEmail(email);
        return toResponse(u);
    }

    public UtilisateurResponse rechercherParUuid(UUID id) {
        return toResponse(find(id));
    }

    public void desactiver(UUID id) {
        find(id).setRecordStatus(RecordStatus.INACTIVE);
    }

    private Utilisateur find(UUID id) {
        return users.findByUuid(id).orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));
    }

    private Role findRole(UUID id) {
        return roles.findByUuid(id).orElseThrow(() -> new ResourceNotFoundException("Rôle introuvable."));
    }

    private String norm(String v) {
        return v.trim().toLowerCase(Locale.ROOT);
    }

    private UtilisateurResponse toResponse(Utilisateur u) {
        return new UtilisateurResponse(u.getUuid(), u.getEntrepriseUuid(), u.getRole().getUuid(), u.getRole().getCode(), u.getNom(), u.getPrenom(), u.getEmail(), u.getProviderAuthentification(), u.getRecordStatus(), u.getCreatedAt(), u.getUpdatedAt());
    }
}