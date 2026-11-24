package com.wokite.net.onboarding.application.external;

import java.util.UUID;

public interface UtilisateurExternalService {

    void rattacherEntreprise(UUID utilisateurUuid, UUID entrepriseUuid);

    void affecterRole(UUID utilisateurUuid, UUID roleUuid);
}