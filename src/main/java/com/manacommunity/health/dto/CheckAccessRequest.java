package com.manacommunity.health.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckAccessRequest {

    private String accessorUserId;
    private String accessorRole;

    @NotBlank(message = "Patient ID is required")
    private String patientId;

    private String recordId;

    @NotBlank(message = "Action is required")
    private String action;
}
