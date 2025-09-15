package com.MindMitra.Rolebased.Repository;

import com.MindMitra.Rolebased.Entity.Journal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JournalRepository extends JpaRepository<Journal, Long> {
    List<Journal> findByUsernameOrderByTimestampDesc(String username);
}
