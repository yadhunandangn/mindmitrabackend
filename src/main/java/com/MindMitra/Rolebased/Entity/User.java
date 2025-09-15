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

import static com.MindMitra.Rolebased.Entity.Role.PATIENT;


@Entity
@Table(name = "UsersTable")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false , unique = true)
    private String username;
    @Column(nullable = false)
    private String FullName;
    @Column(nullable = false , unique = true)
    @Email(message = "Enter valid email")
    private String email;
    @Size(min = 6)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{6,}$",
            message = "Password must be at least 6 characters long, contain at least one uppercase letter, one number, and one special character")
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'PATIENT'")
    @NotNull
    private Role role = PATIENT;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Appointment> appointmentsAsPatient;



}
