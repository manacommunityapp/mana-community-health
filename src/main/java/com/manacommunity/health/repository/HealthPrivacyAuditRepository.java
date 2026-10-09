package com.manacommunity.health.repository;

import com.manacommunity.health.model.HealthPrivacyAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthPrivacyAuditRepository extends JpaRepository<HealthPrivacyAuditEntity, Long> {

    List<HealthPrivacyAuditEntity> findByPatientIdOrderByTimestampDesc(String patientId);
}
