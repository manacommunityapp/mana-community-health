package com.manacommunity.health.service;

import com.manacommunity.health.dto.BookAppointmentRequest;
import com.manacommunity.health.model.HealthAppointmentEntity;
import com.manacommunity.health.model.enums.AppointmentStatus;
import com.manacommunity.health.repository.HealthAppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthBookingService {

    private final HealthAppointmentRepository appointmentRepository;
    private final DoctorVerificationService doctorService;

    @Transactional
    public HealthAppointmentEntity bookSlot(BookAppointmentRequest request) {
        // Slot collision prevention
        boolean isSlotTaken = appointmentRepository.existsByDoctorIdAndSlotTimeAndStatusNot(
                request.getDoctorId(), request.getSlotTime(), AppointmentStatus.CANCELLED);

        if (isSlotTaken) {
            throw new IllegalStateException("Slot is already booked for doctor " + request.getDoctorId()
                    + " at " + request.getSlotTime());
        }

        String apptId = "APT_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();

        HealthAppointmentEntity appointment = HealthAppointmentEntity.builder()
                .appointmentId(apptId)
                .doctorId(request.getDoctorId())
                .patientId(request.getPatientId())
                .patientName(request.getPatientName())
                .bookedByUserId(request.getBookedByUserId())
                .slotTime(request.getSlotTime())
                .mode(request.getMode())
                .status(AppointmentStatus.CONFIRMED)
                .fee(request.getFee())
                .statusHistory("CONFIRMED at " + LocalDateTime.now())
                .build();

        appointment = appointmentRepository.save(appointment);
        log.info("[HEALTH] Appointment {} booked successfully for patient {} with doctor {}",
                apptId, request.getPatientId(), request.getDoctorId());

        doctorService.recordConsultation(request.getDoctorId());
        return appointment;
    }

    @Transactional
    public HealthAppointmentEntity cancelAppointment(String appointmentId, String reason) {
        HealthAppointmentEntity appointment = appointmentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Appointment already cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);
        appointment.setStatusHistory(appointment.getStatusHistory() + " | CANCELLED: " + reason + " at " + LocalDateTime.now());

        log.info("[HEALTH] Appointment {} cancelled. Reason: {}", appointmentId, reason);
        return appointmentRepository.save(appointment);
    }

    @Transactional(readOnly = true)
    public HealthAppointmentEntity getAppointment(String appointmentId) {
        return appointmentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));
    }

    @Transactional(readOnly = true)
    public List<HealthAppointmentEntity> getPatientAppointments(String patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }
}
