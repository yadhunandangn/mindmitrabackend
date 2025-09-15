package com.MindMitra.Rolebased.Service;

import com.MindMitra.Rolebased.DTO.Request.docProfileReq;
import com.MindMitra.Rolebased.DTO.Response.UsersDTO;
import com.MindMitra.Rolebased.Entity.*;
import com.MindMitra.Rolebased.Exception.ResourceNotFoundException;
import com.MindMitra.Rolebased.Repository.AppointmentRepo;
import com.MindMitra.Rolebased.Repository.BlogRepo;
import com.MindMitra.Rolebased.Repository.UsersRepo;
import com.MindMitra.Rolebased.Repository.docRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final docRepo docRepo;
    private final AppointmentRepo appointmentRepo;
    private final BlogRepo blogRepo;
    private final UsersRepo userRepository;   // ✅ to manage admins
    private final PasswordEncoder passwordEncoder;

    // ===============================
    // ✅ ADMIN MANAGEMENT
    // ===============================

    public List<User> getAllAdmins() {
        return userRepository.findByRole(Role.valueOf(Role.ADMIN.name()));
    }

    public User updateAdminProfile(String username, String email) {
        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        admin.setEmail(email);
        return userRepository.save(admin);

    }

    public User changeAdminPassword(String username, String newPassword) {
        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with username: " + username));
        admin.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(admin);
    }

    public User addAdmin(String email, String password, String userName) {
        // ✅ Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Admin with this email already exists.");
        }

        // ✅ Assign defaults for required fields
        if (userName == null || userName.isBlank()) {
            userName = email.split("@")[0]; // default username from email
        }

        String fullName = "Admin User"; // default name for admins

        User admin = User.builder()
                .email(email)
                .username(userName)
                .FullName(fullName) // required field
                .password(passwordEncoder.encode(password))
                .role(Role.ADMIN)   // directly assign enum
                .build();

        return userRepository.save(admin);
    }



    // ===============================
    // ✅ DOCTOR MANAGEMENT
    // ===============================

    public Doctor createDocProfile(docProfileReq request, MultipartFile photo) {
        Doctor doc = Doctor.builder()
                .doctorName(request.getDoctorName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .Specialization(request.getSpecialization())
                .AboutDoc(request.getAboutDoc())
                .role(Role.DOCTOR)
                .build();
        return docRepo.save(doc);
    }

    public Doctor updateDocProfile(Long id, docProfileReq request) {
        Doctor existingDoctor = docRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));

        existingDoctor.setDoctorName(request.getDoctorName());
        existingDoctor.setEmail(request.getEmail());
        existingDoctor.setSpecialization(request.getSpecialization());
        existingDoctor.setAboutDoc(request.getAboutDoc());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingDoctor.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return docRepo.save(existingDoctor);
    }

    public void deleteDocProfile(Long id) {
        if (!docRepo.existsById(id)) {
            throw new ResourceNotFoundException("Doctor not found with id: " + id);
        }
        docRepo.deleteById(id);
    }

    public List<Doctor> getAllDoctors() {
        return docRepo.findAll();
    }

    public Doctor getDoctorById(Long id) {
        return docRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
    }

    // ===============================
    // ✅ APPOINTMENT MANAGEMENT
    // ===============================

    public List<Appointment> getAllAppointments() {
        return appointmentRepo.findAll();
    }

    public Appointment confirmAppointment(Long id) {
        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        appointment.setAppointmentStatus(Status.CONFIRMED);
        return appointmentRepo.save(appointment);
    }

    public Appointment cancelAppointment(Long id) {
        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        appointment.setAppointmentStatus(Status.CANCELLED);
        return appointmentRepo.save(appointment);
    }

    public Appointment updateAppointmentStatus(Long id, Status status) {
        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        appointment.setAppointmentStatus(status);
        return appointmentRepo.save(appointment);
    }

    public List<UsersDTO> getAllusers(){
        List<User> users = userRepository.findByRole(Role.PATIENT);
        return users.stream().map(user -> {
            Long totalAppointments = appointmentRepo.countByUserId(user.getId());
            return new UsersDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    totalAppointments
            );
        }).collect(Collectors.toList());

    }

    public void deleteAdmin(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Admin not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

}
