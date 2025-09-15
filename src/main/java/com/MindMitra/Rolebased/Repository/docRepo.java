package com.MindMitra.Rolebased.Repository;

import com.MindMitra.Rolebased.Entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface docRepo extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByEmail(String email);

    Boolean existsByEmail(String email);

    Optional<Doctor> findByDoctorName(String doctorName);
}
