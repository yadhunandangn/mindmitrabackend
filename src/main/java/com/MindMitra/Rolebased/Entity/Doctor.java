package com.MindMitra.Rolebased.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.MindMitra.Rolebased.Entity.Role.DOCTOR;
import static com.MindMitra.Rolebased.Entity.Role.PATIENT;

@Entity
@Table(name = "DoctorTable")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false , unique = true)
    private String doctorName;

    @Column(nullable = false , unique = true)
    @Email(message = "Enter valid email")
    private String email;

    @Column(nullable = false)
    private String Specialization;

    @Size(min = 6)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{6,}$",
            message = "Password must be at least 6 characters long, contain at least one uppercase letter, one number, and one special character")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'DOCTOR'")
    @NotNull
    private Role role = DOCTOR;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] photo;

    @Column(nullable = false)
    private String AboutDoc;

    // 👇 New mapping
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Appointment> appointmentsAsDoctor;

}
