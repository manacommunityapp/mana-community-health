package com.manacommunity.health.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDoctorRequest {

    @NotBlank(message = "Doctor user ID is required")
    private String userId;

    @NotBlank(message = "Doctor name is required")
    private String name;

    @NotBlank(message = "Medical registration number is mandatory")
    private String registrationNumber;

    @NotBlank(message = "Medical council is required")
    private String medicalCouncil;

    private String specialization;

    @NotNull(message = "Experience years is required")
    private Integer experienceYears;

    private Double consultationFee;
    private String bio;
}
