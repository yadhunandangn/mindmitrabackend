package com.MindMitra.Rolebased.Service;

import com.MindMitra.Rolebased.DTO.Request.BookingReq;
import com.MindMitra.Rolebased.Entity.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PatientController {

    private final BookingService bookingService;

    @PostMapping("/book-appointment")
    public ResponseEntity<String> bookAppointment(@RequestBody BookingReq request, Principal principal) {
        String response = bookingService.bookAppointment(request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getMyBookings(Principal principal) {
        return ResponseEntity.ok(bookingService.getAppointmentsForUser(principal.getName()));
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long id, Principal principal) {
        bookingService.cancelAppointment(id, principal.getName());
        return ResponseEntity.ok("Appointment with ID " + id + " has been cancelled.");
    }
}