package com.manacommunity.health.controller;

import com.manacommunity.common.dto.ApiResponse;
import com.manacommunity.health.dto.BookAppointmentRequest;
import com.manacommunity.health.dto.CancelAppointmentRequest;
import com.manacommunity.health.model.HealthAppointmentEntity;
import com.manacommunity.health.service.HealthBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/health/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final HealthBookingService bookingService;

    @PostMapping("/book")
    public ResponseEntity<ApiResponse<HealthAppointmentEntity>> bookAppointment(
            @Valid @RequestBody BookAppointmentRequest request) {
        log.info("[API] Book appointment for patient {} with doctor {} at {}",
                request.getPatientId(), request.getDoctorId(), request.getSlotTime());
        HealthAppointmentEntity appointment = bookingService.bookSlot(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appointment booked successfully", appointment));
    }

    @PostMapping("/{appointmentId}/cancel")
    public ResponseEntity<ApiResponse<HealthAppointmentEntity>> cancelAppointment(
            @PathVariable String appointmentId,
            @Valid @RequestBody CancelAppointmentRequest request) {
        log.info("[API] Cancel appointment {}", appointmentId);
        HealthAppointmentEntity appointment = bookingService.cancelAppointment(appointmentId, request.getReason());
        return ResponseEntity.ok(ApiResponse.success("Appointment cancelled successfully", appointment));
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<ApiResponse<HealthAppointmentEntity>> getAppointment(
            @PathVariable String appointmentId) {
        HealthAppointmentEntity appointment = bookingService.getAppointment(appointmentId);
        return ResponseEntity.ok(ApiResponse.success("Appointment retrieved", appointment));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<HealthAppointmentEntity>>> getPatientAppointments(
            @PathVariable String patientId) {
        List<HealthAppointmentEntity> list = bookingService.getPatientAppointments(patientId);
        return ResponseEntity.ok(ApiResponse.success("Patient appointments retrieved", list));
    }
}
