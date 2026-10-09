package com.manacommunity.health.repository;

import com.manacommunity.health.model.HealthAppointmentEntity;
import com.manacommunity.health.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HealthAppointmentRepository extends JpaRepository<HealthAppointmentEntity, Long> {

    Optional<HealthAppointmentEntity> findByAppointmentId(String appointmentId);

    boolean existsByDoctorIdAndSlotTimeAndStatusNot(String doctorId, String slotTime, AppointmentStatus status);

    List<HealthAppointmentEntity> findByPatientId(String patientId);

    List<HealthAppointmentEntity> findByDoctorId(String doctorId);
}
