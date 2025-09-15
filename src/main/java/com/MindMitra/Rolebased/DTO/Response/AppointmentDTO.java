package com.MindMitra.Rolebased.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {

    private Long id;             // Appointment ID
    private String userName;     // Patient name
    private String doctorName;   // Doctor name
    private String issue;        // Reason for visit
    private String desc;         // Description
    private String date;         // Appointment date (e.g., "2025-08-24")
    private String time;         // Appointment time (e.g., "14:30")
    private String status;       // PENDING, APPROVED, REJECTED, COMPLETED
}
