package com.manacommunity.health;

import com.manacommunity.health.dto.CheckAccessRequest;
import com.manacommunity.health.model.HealthPrivacyAuditEntity;
import com.manacommunity.health.service.HealthPrivacyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class HealthPrivacyServiceTest {

    @Autowired
    private HealthPrivacyService privacyService;

    @Test
    @DisplayName("Should verify privacy access rights and log audit trails")
    void testPrivacyAccessAndAudit() {
        String patientId = "PAT_555";
        String familyMemberId = "USR_777";
        String strangerId = "USR_999";

        // Patient accessing their own record is allowed
        CheckAccessRequest ownAccess = CheckAccessRequest.builder()
                .accessorUserId(patientId)
                .accessorRole("PATIENT")
                .patientId(patientId)
                .recordId("REC_001")
                .action("VIEW_PRESCRIPTION")
                .build();
        assertTrue(privacyService.checkAndAuditAccess(ownAccess));

        // Unauthorized user access is rejected
        CheckAccessRequest strangerAccess = CheckAccessRequest.builder()
                .accessorUserId(strangerId)
                .accessorRole("RESIDENT")
                .patientId(patientId)
                .recordId("REC_001")
                .action("VIEW_PRESCRIPTION")
                .build();
        assertFalse(privacyService.checkAndAuditAccess(strangerAccess));

        // Grant access to family member
        privacyService.grantAccess(patientId, familyMemberId);

        CheckAccessRequest familyAccess = CheckAccessRequest.builder()
                .accessorUserId(familyMemberId)
                .accessorRole("FAMILY_DELEGATE")
                .patientId(patientId)
                .recordId("REC_001")
                .action("VIEW_PRESCRIPTION")
                .build();
        assertTrue(privacyService.checkAndAuditAccess(familyAccess));

        // Revoke access
        privacyService.revokeAccess(patientId, familyMemberId);
        assertFalse(privacyService.checkAndAuditAccess(familyAccess));

        // Verify audit trail captures all attempts
        List<HealthPrivacyAuditEntity> auditLogs = privacyService.getAuditTrail(patientId);
        assertTrue(auditLogs.size() >= 4);
    }
}
