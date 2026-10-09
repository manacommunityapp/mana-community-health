package com.manacommunity.health;

import com.manacommunity.health.dto.RegisterDoctorRequest;
import com.manacommunity.health.model.DoctorProfileEntity;
import com.manacommunity.health.model.enums.VerificationStatus;
import com.manacommunity.health.service.DoctorVerificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DoctorVerificationServiceTest {

    @Autowired
    private DoctorVerificationService doctorService;

    @Test
    @DisplayName("Should register doctor and verify with badge")
    void testDoctorRegistrationAndVerification() {
        String regNum = "MCI-" + UUID.randomUUID().toString().substring(0, 8);
        RegisterDoctorRequest request = RegisterDoctorRequest.builder()
                .userId("DOC_1001")
                .name("Dr. Arvind Sharma")
                .registrationNumber(regNum)
                .medicalCouncil("Karnataka Medical Council")
                .specialization("General Physician")
                .experienceYears(12)
                .consultationFee(500.0)
                .bio("Experienced community physician")
                .build();

        DoctorProfileEntity profile = doctorService.registerDoctor(request);
        assertNotNull(profile.getId());
        assertEquals(VerificationStatus.SUBMITTED, profile.getStatus());
        assertFalse(profile.isVerifiedBadge());

        // Approve verification
        DoctorProfileEntity verified = doctorService.verifyDoctor("DOC_1001", true);
        assertEquals(VerificationStatus.VERIFIED, verified.getStatus());
        assertTrue(verified.isVerifiedBadge());

        List<DoctorProfileEntity> directory = doctorService.listVerifiedDoctors();
        assertTrue(directory.stream().anyMatch(d -> d.getUserId().equals("DOC_1001")));
    }
}
