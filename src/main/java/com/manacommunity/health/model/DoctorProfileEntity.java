package com.manacommunity.health.model;

import com.manacommunity.common.model.BaseAuditEntity;
import com.manacommunity.health.model.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "health_doctor_profiles", indexes = {
        @Index(name = "idx_doc_user_id", columnList = "user_id", unique = true),
        @Index(name = "idx_doc_reg_num", columnList = "registration_number", unique = true),
        @Index(name = "idx_doc_status", columnList = "status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(name = "doctor_name", nullable = false, length = 128)
    private String name;

    @Column(name = "registration_number", nullable = false, length = 64)
    private String registrationNumber;

    @Column(name = "medical_council", nullable = false, length = 128)
    private String medicalCouncil;

    @Column(name = "specialization", length = 128)
    private String specialization;

    @Column(name = "experience_years", nullable = false)
    private int experienceYears;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    @Builder.Default
    private VerificationStatus status = VerificationStatus.SUBMITTED;

    @Column(name = "verified_badge", nullable = false)
    @Builder.Default
    private boolean verifiedBadge = false;

    @Column(name = "verified_community_consultations", nullable = false)
    @Builder.Default
    private int verifiedCommunityConsultations = 0;

    @Column(name = "consultation_fee")
    private Double consultationFee;

    @Column(name = "bio", length = 512)
    private String bio;
}
