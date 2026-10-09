package com.manacommunity.health.controller;

import com.manacommunity.common.dto.ApiResponse;
import com.manacommunity.health.dto.RegisterDoctorRequest;
import com.manacommunity.health.model.DoctorProfileEntity;
import com.manacommunity.health.service.DoctorVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/health/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorVerificationService doctorService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<DoctorProfileEntity>> registerDoctor(
            @Valid @RequestBody RegisterDoctorRequest request) {
        log.info("[API] Register doctor: {}", request.getRegistrationNumber());
        DoctorProfileEntity profile = doctorService.registerDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Doctor profile registered successfully", profile));
    }

    @PutMapping("/{userId}/verify")
    public ResponseEntity<ApiResponse<DoctorProfileEntity>> verifyDoctor(
            @PathVariable String userId,
            @RequestParam boolean approved) {
        log.info("[API] Verify doctor {}: approved={}", userId, approved);
        DoctorProfileEntity profile = doctorService.verifyDoctor(userId, approved);
        return ResponseEntity.ok(ApiResponse.success("Doctor verification status updated", profile));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<DoctorProfileEntity>> getDoctor(
            @PathVariable String userId) {
        DoctorProfileEntity profile = doctorService.getDoctor(userId);
        return ResponseEntity.ok(ApiResponse.success("Doctor profile retrieved", profile));
    }

    @GetMapping("/verified")
    public ResponseEntity<ApiResponse<List<DoctorProfileEntity>>> listVerifiedDoctors() {
        List<DoctorProfileEntity> verified = doctorService.listVerifiedDoctors();
        return ResponseEntity.ok(ApiResponse.success("Verified doctors retrieved", verified));
    }
}
