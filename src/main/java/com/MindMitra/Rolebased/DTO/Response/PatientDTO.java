package com.MindMitra.Rolebased.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatientDTO {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String issue;       // from Appointment
    private String description; // from Appointment
}
