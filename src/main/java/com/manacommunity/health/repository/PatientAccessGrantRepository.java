package com.manacommunity.health.repository;

import com.manacommunity.health.model.PatientAccessGrantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientAccessGrantRepository extends JpaRepository<PatientAccessGrantEntity, Long> {

    Optional<PatientAccessGrantEntity> findByPatientIdAndAuthorizedUserId(String patientId, String authorizedUserId);

    List<PatientAccessGrantEntity> findByPatientIdAndActiveTrue(String patientId);

    boolean existsByPatientIdAndAuthorizedUserIdAndActiveTrue(String patientId, String authorizedUserId);
}
