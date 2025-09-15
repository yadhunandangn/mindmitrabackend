package com.MindMitra.Rolebased.Repository;

import com.MindMitra.Rolebased.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepo extends JpaRepository<Appointment, Long> {

    boolean existsByDoctor_IdAndAppointmentDateAndAppointmentTime(
            Long doctorId, LocalDate appointmentDate, LocalTime appointmentTime);

    List<Appointment> findByUserId(Long userId);

    List<Appointment> findByUser_Username(String username);

    List<Appointment> findByDoctor_Id(Long doctorId);

    List<Appointment> findByDoctor_DoctorName(String doctorUsername);

    Long countByUserId(Long id);
}
