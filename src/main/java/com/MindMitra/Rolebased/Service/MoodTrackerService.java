package com.MindMitra.Rolebased.Service;

import com.MindMitra.Rolebased.DTO.Request.MoodTrackerRequest;
import com.MindMitra.Rolebased.Entity.MoodEntry;
import com.MindMitra.Rolebased.Entity.User;
import com.MindMitra.Rolebased.Repository.MoodEntryRepository;
import com.MindMitra.Rolebased.Repository.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MoodTrackerService {

    private final MoodEntryRepository moodEntryRepository;
    private final UsersRepo userRepository;

    // Save mood
    public void saveMood(String username, MoodTrackerRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MoodEntry entry = MoodEntry.builder()
                .mood(request.getMood())
                .note(request.getNote())
                .timestamp(LocalDateTime.now())
                .user(user)
                .build();

        moodEntryRepository.save(entry);
    }

    // Fetch all moods
    public List<MoodEntry> getMoodHistory(String username) {
        username = username.trim();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return moodEntryRepository.findByUserOrderByTimestampDesc(user);
    }

    // Fetch latest mood
    public MoodEntry getLatestMood(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return moodEntryRepository.findTopByUserOrderByTimestampDesc(user)
                .orElseThrow(() -> new RuntimeException("No mood entries found."));
    }

    // Delete mood
    public void deleteMood(Long id, String username) {
        MoodEntry entry = moodEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mood entry not found"));

        if (!entry.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can only delete your own moods.");
        }

        moodEntryRepository.delete(entry);
    }
}
