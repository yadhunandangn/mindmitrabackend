package com.MindMitra.Rolebased.Repository;

import com.MindMitra.Rolebased.Entity.OTP;
import com.MindMitra.Rolebased.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.*;

import java.util.Optional;

public interface OTP_Repo extends JpaRepository<OTP, Long> {
    Optional<OTP> findByEmail(String email);


}
