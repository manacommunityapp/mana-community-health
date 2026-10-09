package com.manacommunity.health.dto;

import com.manacommunity.health.model.enums.ConsultationMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookAppointmentRequest {

    @NotBlank(message = "Doctor ID is required")
    private String doctorId;

    @NotBlank(message = "Patient ID is required")
    private String patientId;

    @NotBlank(message = "Patient name is required")
    private String patientName;

    @NotBlank(message = "Booked by user ID is required")
    private String bookedByUserId;

    @NotBlank(message = "Slot time is required")
    private String slotTime;

    @Builder.Default
    private ConsultationMode mode = ConsultationMode.IN_PERSON;

    @NotNull(message = "Fee is required")
    private Double fee;
}
