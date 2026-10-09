package com.manacommunity.health.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelAppointmentRequest {

    @NotBlank(message = "Cancellation reason is required")
    private String reason;
}
