package com.MindMitra.Rolebased.Controller;

import com.MindMitra.Rolebased.DTO.Response.AppointmentDTO;
import com.MindMitra.Rolebased.DTO.Response.PatientDTO;
import com.MindMitra.Rolebased.Entity.Report;
import com.MindMitra.Rolebased.Entity.User;
import com.MindMitra.Rolebased.Repository.ReportRepo;
import com.MindMitra.Rolebased.Repository.UsersRepo;
import com.MindMitra.Rolebased.Service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hcp")
@RequiredArgsConstructor
public class HcpController {

    private final BookingService bookingService;
    private final ReportRepo reportRepo;
    private final UsersRepo usersRepo;

    // ================== Patients ==================
    @GetMapping("/patients")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<PatientDTO>> getPatients() {
        String doctorUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        List<PatientDTO> patients = bookingService.getPatientsForDoctor(doctorUsername);
        return ResponseEntity.ok(patients);
    }

    // ================== Appointments ==================
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/appointments/")
    public ResponseEntity<List<AppointmentDTO>> getAppointments(
            @RequestParam String doctorUsername
    ) {
        try {
            List<AppointmentDTO> appointments = bookingService.getAppointmentsForDoctor(doctorUsername);
            return ResponseEntity.ok(appointments);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/appointments/{appointmentId}/status")
    public ResponseEntity<Void> updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @RequestBody StatusUpdateRequest request
    ) {
        try {
            bookingService.updateAppointmentStatus(appointmentId, request.getStatus());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public static class StatusUpdateRequest {
        private String status;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    // ================== Reports ==================

    // GET: Fetch all reports
    @GetMapping("/reports")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<Report>> getReports() {
        return ResponseEntity.ok(reportRepo.findAll());
    }

    // POST: Create a new report
    @PostMapping("/reports")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Report> createReport(@RequestBody Report report) {
        if (report.getUser() == null || !usersRepo.existsById(report.getUser().getId())) {
            return ResponseEntity.badRequest().build();
        }
        User patient = usersRepo.findById(report.getUser().getId()).get();
        report.setUser(patient);
        return ResponseEntity.ok(reportRepo.save(report));
    }

    // PUT: Update a report
    @PutMapping("/reports/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Report> updateReport(
            @PathVariable Long id,
            @RequestBody Report updatedReport) {
        return reportRepo.findById(id)
                .map(report -> {
                    if (updatedReport.getUser() != null &&
                            usersRepo.existsById(updatedReport.getUser().getId())) {
                        report.setUser(usersRepo.findById(updatedReport.getUser().getId()).get());
                    }
                    report.setTitle(updatedReport.getTitle());
                    report.setNotes(updatedReport.getNotes());
                    return ResponseEntity.ok(reportRepo.save(report));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE: Delete a report
    @DeleteMapping("/reports/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        if (!reportRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        reportRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
