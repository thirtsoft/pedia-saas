package com.wokite.net.utilisateur.application.external;

import com.wokite.net.onboarding.application.external.UtilisateurExternalService;
import com.wokite.net.utilisateur.application.service.UtilisateurService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UtilisateurExternalServiceImpl implements UtilisateurExternalService {

    private final UtilisateurService utilisateurService;

    public UtilisateurExternalServiceImpl(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @Override
    public void rattacherEntreprise(UUID utilisateurUuid, UUID entrepriseUuid) {
        utilisateurService.rattacherEntreprise(utilisateurUuid, entrepriseUuid);
    }

    @Override
    public void affecterRole(UUID utilisateurUuid, UUID roleUuid) {
        utilisateurService.affecterRole(utilisateurUuid, roleUuid);
    }
}