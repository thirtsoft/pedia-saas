package com.wokite.net.onboarding.domain.repository;

import com.wokite.net.onboarding.domain.model.Onboarding;
import com.wokite.net.onboarding.domain.model.OnboardingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OnboardingRepository extends JpaRepository<Onboarding, Long> {

    Optional<Onboarding> findByUuid(UUID uuid);

    Optional<Onboarding> findByUtilisateurUuid(UUID utilisateurUuid);

    boolean existsByUtilisateurUuid(UUID utilisateurUuid);

    boolean existsByUtilisateurUuidAndOnboardingStatus(UUID utilisateurUuid, OnboardingStatus onboardingStatus);
}