package com.MindMitra.Rolebased.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "OTP_TABLE")
@Data
@NoArgsConstructor
@AllArgsConstructor


public class OTP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    @Column(nullable = false , unique = true)
    private String email;
    @Column(nullable=false)
    private String otp;
    @Column
    private LocalDateTime expiry;
    @Column(nullable = true)
    private Boolean verified;
}
