package com.manacommunity.health.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "health_privacy_audit_logs", indexes = {
        @Index(name = "idx_hpa_patient_id", columnList = "patient_id"),
        @Index(name = "idx_hpa_accessor", columnList = "accessor_user_id"),
        @Index(name = "idx_hpa_timestamp", columnList = "timestamp")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthPrivacyAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "audit_id", nullable = false, unique = true, length = 64)
    private String auditId;

    @Column(name = "accessor_user_id", length = 64)
    private String accessorUserId;

    @Column(name = "accessor_role", length = 64)
    private String accessorRole;

    @Column(name = "patient_id", nullable = false, length = 64)
    private String patientId;

    @Column(name = "record_id", length = 64)
    private String recordId;

    @Column(name = "action", nullable = false, length = 64)
    private String action;

    @Column(name = "timestamp", nullable = false)
    @Builder.Default
    private Instant timestamp = Instant.now();

    @Column(name = "authorized", nullable = false)
    private boolean authorized;
}
