package com.wokite.net.utilisateur.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.wokite.net.shared.security.GoogleAuthProperties;
import com.wokite.net.utilisateur.auth.exception.GoogleTokenInvalideException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleTokenVerifierService {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifierService(GoogleAuthProperties properties) {

        if (properties.clientId() == null || properties.clientId().isBlank()) {
            throw new IllegalStateException(
                    "La configuration GOOGLE_CLIENT_ID est obligatoire."
            );
        }

        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(properties.clientId()))
                .build();
    }

    public GoogleIdToken.Payload verifier(String idToken) {

        try {
            GoogleIdToken googleIdToken = verifier.verify(idToken);

            if (googleIdToken == null) {
                throw new GoogleTokenInvalideException();
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();

            if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
                throw new GoogleTokenInvalideException();
            }

            return payload;

        } catch (GoogleTokenInvalideException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new GoogleTokenInvalideException();
        }
    }
}