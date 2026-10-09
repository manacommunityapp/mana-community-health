package com.manacommunity.health.service;

import com.manacommunity.health.dto.RegisterDoctorRequest;
import com.manacommunity.health.model.DoctorProfileEntity;
import com.manacommunity.health.model.enums.VerificationStatus;
import com.manacommunity.health.repository.DoctorProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorVerificationService {

    private final DoctorProfileRepository doctorRepository;

    @Transactional
    public DoctorProfileEntity registerDoctor(RegisterDoctorRequest request) {
        if (request.getRegistrationNumber() == null || request.getRegistrationNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Medical registration number is mandatory");
        }

        doctorRepository.findByUserId(request.getUserId()).ifPresent(d -> {
            throw new IllegalStateException("Doctor profile already exists for user: " + request.getUserId());
        });

        doctorRepository.findByRegistrationNumber(request.getRegistrationNumber()).ifPresent(d -> {
            throw new IllegalStateException("Medical registration number already in use: " + request.getRegistrationNumber());
        });

        DoctorProfileEntity doctor = DoctorProfileEntity.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .registrationNumber(request.getRegistrationNumber())
                .medicalCouncil(request.getMedicalCouncil())
                .specialization(request.getSpecialization())
                .experienceYears(request.getExperienceYears())
                .consultationFee(request.getConsultationFee() != null ? request.getConsultationFee() : 0.0)
                .bio(request.getBio())
                .status(VerificationStatus.SUBMITTED)
                .verifiedBadge(false)
                .verifiedCommunityConsultations(0)
                .build();

        log.info("[HEALTH] Registered doctor profile for user {} (Reg: {})", request.getUserId(), request.getRegistrationNumber());
        return doctorRepository.save(doctor);
    }

    @Transactional
    public DoctorProfileEntity verifyDoctor(String userId, boolean approved) {
        DoctorProfileEntity doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found: " + userId));

        if (approved) {
            doctor.setStatus(VerificationStatus.VERIFIED);
            doctor.setVerifiedBadge(true);
            log.info("[HEALTH] Doctor {} verified successfully with verified badge", userId);
        } else {
            doctor.setStatus(VerificationStatus.REJECTED);
            doctor.setVerifiedBadge(false);
            log.info("[HEALTH] Doctor {} verification rejected", userId);
        }
        return doctorRepository.save(doctor);
    }

    @Transactional
    public void recordConsultation(String userId) {
        doctorRepository.findByUserId(userId).ifPresent(doc -> {
            doc.setVerifiedCommunityConsultations(doc.getVerifiedCommunityConsultations() + 1);
            doctorRepository.save(doc);
        });
    }

    @Transactional(readOnly = true)
    public DoctorProfileEntity getDoctor(String userId) {
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + userId));
    }

    @Transactional(readOnly = true)
    public List<DoctorProfileEntity> listVerifiedDoctors() {
        return doctorRepository.findByVerifiedBadgeTrue();
    }
}
