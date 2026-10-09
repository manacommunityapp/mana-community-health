package com.manacommunity.health.model;

import com.manacommunity.common.model.BaseAuditEntity;
import com.manacommunity.health.model.enums.AppointmentStatus;
import com.manacommunity.health.model.enums.ConsultationMode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "health_appointments", indexes = {
        @Index(name = "idx_appt_appointment_id", columnList = "appointment_id", unique = true),
        @Index(name = "idx_appt_slot_key", columnList = "doctor_id, slot_time"),
        @Index(name = "idx_appt_patient", columnList = "patient_id"),
        @Index(name = "idx_appt_doctor", columnList = "doctor_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthAppointmentEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_id", nullable = false, unique = true, length = 64)
    private String appointmentId;

    @Column(name = "doctor_id", nullable = false, length = 64)
    private String doctorId;

    @Column(name = "patient_id", nullable = false, length = 64)
    private String patientId;

    @Column(name = "patient_name", nullable = false, length = 128)
    private String patientName;

    @Column(name = "booked_by_user_id", nullable = false, length = 64)
    private String bookedByUserId;

    @Column(name = "slot_time", nullable = false, length = 64)
    private String slotTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false, length = 32)
    @Builder.Default
    private ConsultationMode mode = ConsultationMode.IN_PERSON;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.CONFIRMED;

    @Column(name = "fee", nullable = false)
    private double fee;

    @Column(name = "status_history", columnDefinition = "TEXT")
    private String statusHistory;

    @Column(name = "cancellation_reason", length = 256)
    private String cancellationReason;

    @Column(name = "meeting_link", length = 512)
    private String meetingLink;
}
