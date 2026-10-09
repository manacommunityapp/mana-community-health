package com.manacommunity.health.service;

import com.manacommunity.health.dto.CheckAccessRequest;
import com.manacommunity.health.model.HealthPrivacyAuditEntity;
import com.manacommunity.health.model.PatientAccessGrantEntity;
import com.manacommunity.health.repository.HealthPrivacyAuditRepository;
import com.manacommunity.health.repository.PatientAccessGrantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthPrivacyService {

    private final PatientAccessGrantRepository grantRepository;
    private final HealthPrivacyAuditRepository auditRepository;

    @Transactional
    public void grantAccess(String patientId, String authorizedUserId) {
        var existing = grantRepository.findByPatientIdAndAuthorizedUserId(patientId, authorizedUserId);
        if (existing.isPresent()) {
            PatientAccessGrantEntity grant = existing.get();
            grant.setActive(true);
            grant.setGrantedAt(LocalDateTime.now());
            grant.setRevokedAt(null);
            grantRepository.save(grant);
        } else {
            PatientAccessGrantEntity grant = PatientAccessGrantEntity.builder()
                    .patientId(patientId)
                    .authorizedUserId(authorizedUserId)
                    .active(true)
                    .grantedAt(LocalDateTime.now())
                    .build();
            grantRepository.save(grant);
        }
        log.info("[PRIVACY] Granted record access for patient {} to user {}", patientId, authorizedUserId);
    }

    @Transactional
    public void revokeAccess(String patientId, String authorizedUserId) {
        grantRepository.findByPatientIdAndAuthorizedUserId(patientId, authorizedUserId).ifPresent(grant -> {
            grant.setActive(false);
            grant.setRevokedAt(LocalDateTime.now());
            grantRepository.save(grant);
            log.info("[PRIVACY] Revoked record access for patient {} from user {}", patientId, authorizedUserId);
        });
    }

    @Transactional
    public boolean checkAndAuditAccess(CheckAccessRequest request) {
        boolean isOwner = request.getAccessorUserId() != null && request.getAccessorUserId().equals(request.getPatientId());
        boolean isDelegated = request.getAccessorUserId() != null &&
                grantRepository.existsByPatientIdAndAuthorizedUserIdAndActiveTrue(
                        request.getPatientId(), request.getAccessorUserId());

        boolean allowed = isOwner || isDelegated;

        HealthPrivacyAuditEntity auditEntry = HealthPrivacyAuditEntity.builder()
                .auditId(UUID.randomUUID().toString())
                .accessorUserId(request.getAccessorUserId())
                .accessorRole(request.getAccessorRole())
                .patientId(request.getPatientId())
                .recordId(request.getRecordId())
                .action(request.getAction())
                .timestamp(Instant.now())
                .authorized(allowed)
                .build();

        auditRepository.save(auditEntry);
        log.info("[PRIVACY AUDIT] Access by {} on patient {} record {}: {}",
                request.getAccessorUserId(), request.getPatientId(), request.getRecordId(), allowed ? "GRANTED" : "DENIED");

        return allowed;
    }

    @Transactional(readOnly = true)
    public List<HealthPrivacyAuditEntity> getAuditTrail(String patientId) {
        return auditRepository.findByPatientIdOrderByTimestampDesc(patientId);
    }

    @Transactional(readOnly = true)
    public List<PatientAccessGrantEntity> getActiveGrants(String patientId) {
        return grantRepository.findByPatientIdAndActiveTrue(patientId);
    }
}
