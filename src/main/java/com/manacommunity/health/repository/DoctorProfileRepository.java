package com.manacommunity.health.repository;

import com.manacommunity.health.model.DoctorProfileEntity;
import com.manacommunity.health.model.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfileEntity, Long> {

    Optional<DoctorProfileEntity> findByUserId(String userId);

    Optional<DoctorProfileEntity> findByRegistrationNumber(String registrationNumber);

    List<DoctorProfileEntity> findByStatus(VerificationStatus status);

    List<DoctorProfileEntity> findByVerifiedBadgeTrue();
}
