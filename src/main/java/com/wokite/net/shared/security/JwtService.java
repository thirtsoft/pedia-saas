package com.wokite.net.shared.security;


import com.wokite.net.onboarding.domain.model.OnboardingStatus;
import com.wokite.net.onboarding.domain.model.OnboardingStepCode;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final JwtProperties properties;
    private final SecretKey signingKey;

    @SuppressFBWarnings(
            value = "CT_CONSTRUCTOR_THROW",
            justification = "Une configuration JWT invalide doit empêcher le démarrage de l'application."
    )
    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.signingKey = createSigningKey(properties.secret());
    }

    public String generateToken(AuthenticatedUser user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(properties.expiration());

        return Jwts.builder()
                .subject(user.utilisateurUuid().toString())
                .claim("entrepriseUuid", user.entrepriseUuid() == null ? null : user.entrepriseUuid().toString())
                .claim("role", user.role())
                .claim("permissions", user.permissions())
                .claim("onboardingStatus", user.onboardingStatus() == null ? null : user.onboardingStatus().name())
                .claim("onboardingStep", user.onboardingStep() == null ? null : user.onboardingStep().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey)
                .compact();
    }

    public AuthenticatedUser parseUser(String token) {
        Claims claims = Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
        UUID utilisateurUuid = UUID.fromString(claims.getSubject());
        UUID entrepriseUuid = parseUuid(claims.get("entrepriseUuid"));
        String role = claims.get("role", String.class);
        Set<String> permissions = extractPermissions(claims.get("permissions"));
        OnboardingStatus onboardingStatus = parseOnboardingStatus(claims.get("onboardingStatus"));
        OnboardingStepCode onboardingStep = parseOnboardingStep(claims.get("onboardingStep"));

        return new AuthenticatedUser(utilisateurUuid, entrepriseUuid, role, permissions, onboardingStatus, onboardingStep);
    }

    private static SecretKey createSigningKey(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "La clé secrète JWT est obligatoire."
            );
        }

        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (secretBytes.length < 32) {
            throw new IllegalStateException(
                    "La clé secrète JWT doit contenir au moins 32 octets."
            );
        }

        return Keys.hmacShaKeyFor(secretBytes);
    }

    private UUID parseUuid(Object value) {
        if (value == null) {
            return null;
        }
        return UUID.fromString(value.toString());
    }

    private OnboardingStatus parseOnboardingStatus(Object value) {
        if (value == null) {
            return null;
        }

        return OnboardingStatus.valueOf(value.toString());
    }

    private OnboardingStepCode parseOnboardingStep(Object value) {
        if (value == null) {
            return null;
        }
        return OnboardingStepCode.valueOf(value.toString());
    }

    private Set<String> extractPermissions(Object value) {
        if (!(value instanceof List<?> list)) {
            return Set.of();
        }

        return list.stream().map(Object::toString).collect(Collectors.toUnmodifiableSet());
    }
}