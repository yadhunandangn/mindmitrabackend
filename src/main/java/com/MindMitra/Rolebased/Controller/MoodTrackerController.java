package com.MindMitra.Rolebased.Controller;

import com.MindMitra.Rolebased.DTO.Request.MoodTrackerRequest;
import com.MindMitra.Rolebased.Entity.MoodEntry;
import com.MindMitra.Rolebased.Service.MoodTrackerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/auth/mood-tracker")
@RequiredArgsConstructor
public class MoodTrackerController {

    private final MoodTrackerService moodTrackerService;

    // Save mood
    @PostMapping
    public ResponseEntity<String> saveMood(@RequestBody MoodTrackerRequest request, Principal principal) {
        moodTrackerService.saveMood(principal.getName(), request);
        return ResponseEntity.ok("Mood saved successfully.");
    }

    // Get all moods for a user
    @GetMapping("/{username}")
    public ResponseEntity<List<MoodEntry>> getMoodHistory(@PathVariable String username) {
        System.out.println("Fetching mood history for: " + username);
        return ResponseEntity.ok(moodTrackerService.getMoodHistory(username));
    }


    // Get latest mood
    @GetMapping("/{username}/latest")
    public ResponseEntity<MoodEntry> getLatestMood(@PathVariable String username) {
        return ResponseEntity.ok(moodTrackerService.getLatestMood(username));
    }

    // Delete a mood
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMood(@PathVariable Long id, Principal principal) {
        moodTrackerService.deleteMood(id, principal.getName());
        return ResponseEntity.ok("Mood deleted successfully.");
    }
}
