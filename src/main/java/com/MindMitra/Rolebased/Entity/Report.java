package com.MindMitra.Rolebased.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // Link to User entity
    @JoinColumn(name = "patient_id", nullable = false)
    private User user;  // The patient this report belongs to

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String notes;
}
