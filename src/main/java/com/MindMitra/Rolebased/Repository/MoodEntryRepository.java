package com.MindMitra.Rolebased.Repository;

import com.MindMitra.Rolebased.Entity.MoodEntry;
import com.MindMitra.Rolebased.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
    List<MoodEntry> findByUserOrderByTimestampDesc(User user);
    Optional<MoodEntry> findTopByUserOrderByTimestampDesc(User user);
}
