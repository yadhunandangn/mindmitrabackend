package com.MindMitra.Rolebased.Controller;

import com.MindMitra.Rolebased.DTO.Request.docProfileReq;
import com.MindMitra.Rolebased.DTO.Response.UsersDTO;
import com.MindMitra.Rolebased.Entity.*;
import com.MindMitra.Rolebased.Service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin(
        origins = "http://localhost:5173",          // frontend URL
        allowedHeaders = {"Authorization", "Content-Type"}, // allow JWT header
        allowCredentials = "true")
public class AdminController {

    private final AdminService adminService;

    // ===============================
    // ✅ ADMIN MANAGEMENT
    // ===============================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    // ✅ safer: no ID from frontend, fetch current admin from JWT
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-profile")
    public ResponseEntity<User> updateProfile(
            Authentication authentication,
            @RequestBody Map<String, String> request
    ) {
        String username = authentication.getName(); // current logged-in admin
        String email = request.get("email");

        return ResponseEntity.ok(adminService.updateAdminProfile(username, email));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/change-password")
    public ResponseEntity<User> changePassword(
            Authentication authentication,
            @RequestBody Map<String, String> request
    ) {
        String username = authentication.getName(); // logged-in username from JWT
        String password = request.get("password");
        return ResponseEntity.ok(adminService.changeAdminPassword(username, password));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<User> addAdmin(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String username = request.get("username");

        if (email == null || password == null || username == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(adminService.addAdmin(email, password, username));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.ok("Admin with ID " + id + " deleted successfully.");
    }

    // ===============================
    // ✅ DOCTOR MANAGEMENT
    // ===============================

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/doctors", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Doctor> createDoctorProfile(
            @RequestPart("data") docProfileReq request,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        return ResponseEntity.ok(adminService.createDocProfile(request, photo));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/doctors/{id}")
    public ResponseEntity<Doctor> updateDoctorProfile(@PathVariable Long id, @RequestBody docProfileReq request) {
        return ResponseEntity.ok(adminService.updateDocProfile(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<String> deleteDoctorProfile(@PathVariable Long id) {
        adminService.deleteDocProfile(id);
        return ResponseEntity.ok("Doctor with ID " + id + " deleted successfully.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/doctors")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(adminService.getAllDoctors());
    }

    // ===============================
    // ✅ APPOINTMENT MANAGEMENT
    // ===============================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(adminService.getAllAppointments());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/appointments/{id}/status")
    public ResponseEntity<Appointment> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        String statusStr = request.get("status");
        Status status = Status.valueOf(statusStr.toUpperCase()); // PENDING / CONFIRMED / CANCELLED
        return ResponseEntity.ok(adminService.updateAppointmentStatus(id, status));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UsersDTO>> getAllPatients(){

        List<UsersDTO> users =  adminService.getAllusers();
        return ResponseEntity.ok(users);

    }




}
