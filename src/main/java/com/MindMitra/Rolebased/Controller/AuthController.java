package com.MindMitra.Rolebased.Controller;

import com.MindMitra.Rolebased.DTO.Request.LoginReq;
import com.MindMitra.Rolebased.DTO.Request.BookingReq;
import com.MindMitra.Rolebased.DTO.Response.LoginRes;
import com.MindMitra.Rolebased.Entity.Appointment;
import com.MindMitra.Rolebased.Exception.OtpExpiredException;
import com.MindMitra.Rolebased.Exception.OtpMismatchException;
import com.MindMitra.Rolebased.Service.profileService;
import com.MindMitra.Rolebased.Service.BookingService;
import jakarta.persistence.PrePersist;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "https://mindmitra-platform.vercel.app")
@RequiredArgsConstructor
public class AuthController {

    private final profileService authService;
    private final BookingService bookingService;



    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        // Check if email is provided
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        try {
            // Call service to send OTP
            String result = authService.sendOTP(email);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            // Handle known runtime exceptions from service
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send OTP: " + e.getMessage());
        }
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        // Validate input
        if (email == null || email.isEmpty() || otp == null || otp.isEmpty()) {
            return ResponseEntity.badRequest().body("Email and OTP are required");
        }

        try {
            authService.verifyOTP(otp, email);
            return ResponseEntity.ok("OTP verified successfully");
        } catch (OtpExpiredException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP expired");
        } catch (OtpMismatchException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Verification failed: " + e.getMessage());
        }
    }

    @PostMapping("/create-profile")
    public ResponseEntity<String> createProfile(@RequestBody Map<String , String> Request)
    {
        String username=Request.get("username");
        String fullName=Request.get("fullName");
        String password = Request.get("password");
        String email = Request.get("email");
        if(username==null || password==null || fullName==null || email==null)
        {
            return ResponseEntity.badRequest().body("Kindly Fill all the fields");
        }
        try
        {
            authService.createProfile(email , username,fullName,password);
            return ResponseEntity.ok("Profile Created Successfully");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping("/login")
    public LoginRes login(@RequestBody LoginReq Request)
    {
        return authService.loginAuth(Request);
    }

    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping("/book-appointment")
    public String bookAppointment(@RequestBody BookingReq request)
    {
        String response = bookingService.bookAppointment(request, request.getUserName());
        return response;

    }

    @PostMapping("/doctor-login")
    public LoginRes DocLogin(@RequestBody LoginReq Request)
    {
        return authService.DocLoginAuth(Request);
    }


    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/get-appointments/{username}")
    public ResponseEntity<List<BookingReq>> getAppointments(@PathVariable String username) {
        try {
            // Fetch Appointment entities
            List<Appointment> appointments = bookingService.getAppointmentsByUser(username);

            // Map entities to BookingReq DTOs including status and doctor name
            List<BookingReq> appointmentDTOs = appointments.stream().map(app -> {
                BookingReq dto = new BookingReq();
                dto.setDocID(app.getDoctor().getId());
                dto.setDoctorName(app.getDoctor().getDoctorName()); // <-- Added doctor name
                dto.setUserName(app.getUser().getUsername());
                dto.setDate(app.getAppointmentDate());
                dto.setTime(app.getAppointmentTime());
                dto.setIssue(app.getIssue());
                dto.setDesc(app.getDescription());
                dto.setStatus(app.getAppointmentStatus().name()); // Assuming status is Enum
                return dto;
            }).toList();

            return ResponseEntity.ok(appointmentDTOs);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        try {
            // Generate OTP or reset link
            String result = authService.initiatePasswordReset(email);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process forgot password: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");

        if (email == null || otp == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Email, OTP, and new password are required");
        }

        try {
            String result = authService.resetPassword(email, otp, newPassword);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }






}
