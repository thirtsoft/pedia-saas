package com.wokite.net.utilisateur.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(

        @NotBlank(message = "Le token Google est obligatoire.")
        String idToken

) {
}