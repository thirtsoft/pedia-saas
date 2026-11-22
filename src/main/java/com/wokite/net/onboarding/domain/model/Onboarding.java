package com.wokite.net.onboarding.domain.model;

import com.wokite.net.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "onboarding",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_onboarding_utilisateur_uuid",
                        columnNames = "utilisateur_uuid"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Onboarding extends BaseEntity {

    @Column(name = "entreprise_uuid", updatable = false)
    private UUID entrepriseUuid;

    @Column(name = "utilisateur_uuid", nullable = false, updatable = false)
    private UUID utilisateurUuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "onboarding_status", nullable = false, length = 20)
    private OnboardingStatus onboardingStatus = OnboardingStatus.EN_COURS;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_step", nullable = false, length = 50)
    private OnboardingStepCode currentStep = OnboardingStepCode.CHOIX_ROLE;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "last_activity_at", nullable = false)
    private Instant lastActivityAt;

    @Column(name = "error_code", length = 100)
    private String errorCode;

    @Column(name = "error_message", length = 500)
    private String errorMessage;
}