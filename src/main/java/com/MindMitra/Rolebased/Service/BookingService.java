package com.MindMitra.Rolebased.Service;

import com.MindMitra.Rolebased.DTO.Request.BookingReq;
import com.MindMitra.Rolebased.DTO.Response.AppointmentDTO;
import com.MindMitra.Rolebased.DTO.Response.PatientDTO;
import com.MindMitra.Rolebased.Entity.Appointment;
import com.MindMitra.Rolebased.Entity.Doctor;
import com.MindMitra.Rolebased.Entity.Status;
import com.MindMitra.Rolebased.Entity.User;
import com.MindMitra.Rolebased.Exception.DoctorAlreadyBookedException;
import com.MindMitra.Rolebased.Exception.ResourceNotFoundException;
import com.MindMitra.Rolebased.Repository.AppointmentRepo;
import com.MindMitra.Rolebased.Repository.UsersRepo;
import com.MindMitra.Rolebased.Repository.docRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final UsersRepo usersRepo;
    private final docRepo docRepo;
    private final AppointmentRepo appointmentRepo;

    public String bookAppointment(BookingReq request, String username) {
        User patient = usersRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + username));

        Doctor doctor = docRepo.findById(request.getDocID())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id " + request.getDocID()));

        LocalDate appointmentDate = request.getDate();
        LocalTime appointmentTime = request.getTime();

        // Check if doctor already booked
        boolean alreadyBooked = appointmentRepo
                .existsByDoctor_IdAndAppointmentDateAndAppointmentTime(
                        doctor.getId(), appointmentDate, appointmentTime);

        if (alreadyBooked) {
            throw new DoctorAlreadyBookedException(
                    "Doctor " + doctor.getDoctorName() + " is already booked at this time."
            );
        }

        Appointment book = Appointment.builder()
                .user(patient)
                .doctor(doctor)
                .userName(patient.getUsername())
                .issue(request.getIssue())
                .description(request.getDesc())
                .appointmentDate(appointmentDate)
                .appointmentTime(appointmentTime)
                .appointmentStatus(Status.PENDING)
                .build();

        appointmentRepo.save(book);

        return "Booking request sent for " + patient.getUsername() +
                " on " + appointmentDate + " at " + appointmentTime;
    }

    public List<Appointment> getAppointmentsForUser(String username) {
        User user = usersRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return appointmentRepo.findByUserId(user.getId());
    }

    public void cancelAppointment(Long appointmentId, String username) {
        User user = usersRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (!appointment.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User not authorized to cancel this appointment.");
        }

        appointmentRepo.delete(appointment);
    }


    public List<Appointment> getAppointmentsByUser(String username) {
        return appointmentRepo.findByUser_Username(username);

    }

    public List<PatientDTO> getPatientsForDoctor(String doctorUsername) {
        // Find doctor entity
        Doctor doctor = docRepo.findByDoctorName(doctorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        // Get all appointments for this doctor
        List<Appointment> appointments = appointmentRepo.findByDoctor_Id(doctor.getId());

        // Map each appointment to PatientDTO including issue and description
        return appointments.stream()
                .map(appt -> new PatientDTO(
                        appt.getUser().getId(),
                        appt.getUser().getUsername(),
                        appt.getUser().getFullName(),
                        appt.getUser().getEmail(),
                        appt.getIssue(),
                        appt.getDescription()
                ))
                .toList();
    }



    public List<AppointmentDTO> getAppointmentsForDoctor(String doctorUsername) {
        // Fetch all appointments for the doctor

        Optional<Doctor> doctor=docRepo.findByDoctorName(doctorUsername);

        List<Appointment> appointments = appointmentRepo.findByDoctor_Id(doctor.get().getId());

        // Map to DTO
        return appointments.stream()
                .map(appt -> new AppointmentDTO(
                        appt.getId(),
                        appt.getUser().getFullName(),        // Patient name
                        doctor.get().getDoctorName(),              // Doctor name
                        appt.getIssue(),
                        appt.getDescription(),
                        appt.getAppointmentDate().toString(), // Assuming LocalDate
                        appt.getAppointmentTime().toString(), // Assuming LocalTime
                        appt.getAppointmentStatus().name()    // Enum to String
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public Appointment updateAppointmentStatus(Long appointmentId, String status) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));

        // Assuming status is an Enum
        appointment.setAppointmentStatus(Status.CONFIRMED);

        return appointmentRepo.save(appointment);
    }

}
