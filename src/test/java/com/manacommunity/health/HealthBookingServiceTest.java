package com.manacommunity.health;

import com.manacommunity.health.dto.BookAppointmentRequest;
import com.manacommunity.health.dto.RegisterDoctorRequest;
import com.manacommunity.health.model.HealthAppointmentEntity;
import com.manacommunity.health.model.enums.AppointmentStatus;
import com.manacommunity.health.model.enums.ConsultationMode;
import com.manacommunity.health.service.DoctorVerificationService;
import com.manacommunity.health.service.HealthBookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class HealthBookingServiceTest {

    @Autowired
    private HealthBookingService bookingService;

    @Autowired
    private DoctorVerificationService doctorService;

    @Test
    @DisplayName("Should book slot and prevent double booking of same doctor at same time")
    void testBookingAndCollisionPrevention() {
        String docId = "DOC_" + UUID.randomUUID().toString().substring(0, 8);
        doctorService.registerDoctor(RegisterDoctorRequest.builder()
                .userId(docId)
                .name("Dr. Priya Rao")
                .registrationNumber("REG-" + UUID.randomUUID().toString().substring(0, 8))
                .medicalCouncil("Tamil Nadu Medical Council")
                .experienceYears(8)
                .build());

        String slotTime = "2026-10-15T10:00:00";
        BookAppointmentRequest req1 = BookAppointmentRequest.builder()
                .doctorId(docId)
                .patientId("PAT_101")
                .patientName("Ramesh Kumar")
                .bookedByUserId("PAT_101")
                .slotTime(slotTime)
                .mode(ConsultationMode.VIDEO)
                .fee(400.0)
                .build();

        HealthAppointmentEntity appt1 = bookingService.bookSlot(req1);
        assertNotNull(appt1.getAppointmentId());
        assertEquals(AppointmentStatus.CONFIRMED, appt1.getStatus());

        // Booking the same doctor at the same slot should fail
        BookAppointmentRequest req2 = BookAppointmentRequest.builder()
                .doctorId(docId)
                .patientId("PAT_102")
                .patientName("Suresh Nair")
                .bookedByUserId("PAT_102")
                .slotTime(slotTime)
                .mode(ConsultationMode.IN_PERSON)
                .fee(400.0)
                .build();

        assertThrows(IllegalStateException.class, () -> bookingService.bookSlot(req2));

        // Cancelling appt1 frees up the slot
        bookingService.cancelAppointment(appt1.getAppointmentId(), "Patient unavailable");
        HealthAppointmentEntity cancelled = bookingService.getAppointment(appt1.getAppointmentId());
        assertEquals(AppointmentStatus.CANCELLED, cancelled.getStatus());

        // Now req2 can book the slot
        HealthAppointmentEntity appt2 = bookingService.bookSlot(req2);
        assertNotNull(appt2.getAppointmentId());
        assertEquals(AppointmentStatus.CONFIRMED, appt2.getStatus());
    }
}
