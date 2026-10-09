package com.manacommunity.health.controller;

import com.manacommunity.common.dto.ApiResponse;
import com.manacommunity.health.dto.CheckAccessRequest;
import com.manacommunity.health.dto.GrantAccessRequest;
import com.manacommunity.health.model.HealthPrivacyAuditEntity;
import com.manacommunity.health.model.PatientAccessGrantEntity;
import com.manacommunity.health.service.HealthPrivacyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/health/privacy")
@RequiredArgsConstructor
public class HealthPrivacyController {

    private final HealthPrivacyService privacyService;

    @PostMapping("/grants")
    public ResponseEntity<ApiResponse<String>> grantAccess(
            @Valid @RequestBody GrantAccessRequest request) {
        log.info("[API] Granting patient {} access to user {}", request.getPatientId(), request.getAuthorizedUserId());
        privacyService.grantAccess(request.getPatientId(), request.getAuthorizedUserId());
        return ResponseEntity.ok(ApiResponse.success("Access granted successfully", "GRANTED"));
    }

    @DeleteMapping("/grants")
    public ResponseEntity<ApiResponse<String>> revokeAccess(
            @RequestParam String patientId,
            @RequestParam String authorizedUserId) {
        log.info("[API] Revoking patient {} access from user {}", patientId, authorizedUserId);
        privacyService.revokeAccess(patientId, authorizedUserId);
        return ResponseEntity.ok(ApiResponse.success("Access revoked successfully", "REVOKED"));
    }

    @PostMapping("/check-access")
    public ResponseEntity<ApiResponse<Boolean>> checkAccess(
            @Valid @RequestBody CheckAccessRequest request) {
        boolean allowed = privacyService.checkAndAuditAccess(request);
        return ResponseEntity.ok(ApiResponse.success(
                allowed ? "Access authorized" : "Access denied", allowed));
    }

    @GetMapping("/audit/{patientId}")
    public ResponseEntity<ApiResponse<List<HealthPrivacyAuditEntity>>> getAuditTrail(
            @PathVariable String patientId) {
        List<HealthPrivacyAuditEntity> logs = privacyService.getAuditTrail(patientId);
        return ResponseEntity.ok(ApiResponse.success("Audit trail retrieved", logs));
    }

    @GetMapping("/grants/{patientId}")
    public ResponseEntity<ApiResponse<List<PatientAccessGrantEntity>>> getActiveGrants(
            @PathVariable String patientId) {
        List<PatientAccessGrantEntity> grants = privacyService.getActiveGrants(patientId);
        return ResponseEntity.ok(ApiResponse.success("Active access grants retrieved", grants));
    }
}
