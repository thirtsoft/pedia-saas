package com.wokite.net.utilisateur.auth;

import com.wokite.net.utilisateur.application.dto.InscriptionResponse;
import com.wokite.net.utilisateur.application.dto.InscrireUtilisateurRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthentificationController {

    private final AuthentificationService service;

    public AuthentificationController(AuthentificationService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<InscriptionResponse> inscrire(@Valid @RequestBody InscrireUtilisateurRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.inscrire(request));
    }
}