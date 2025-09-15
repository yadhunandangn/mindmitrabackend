package com.MindMitra.Rolebased.Controller;

import com.MindMitra.Rolebased.Entity.Journal;
import com.MindMitra.Rolebased.Service.JournalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth/journals")
@RequiredArgsConstructor
public class JournalController {

    private final JournalService journalService;

    // GET /auth/journals/{username}
    @GetMapping("/{username}")
    public ResponseEntity<List<Journal>> getJournals(@PathVariable String username) {
        return ResponseEntity.ok(journalService.getJournalsByUsername(username));
    }

    // POST /auth/journals
    @PostMapping
    public ResponseEntity<String> createJournal(@RequestBody Map<String, String> request, Principal principal) {
        String username = principal.getName(); // take logged-in user from JWT
        journalService.createJournal(username, request.get("title"), request.get("content"));
        return ResponseEntity.ok("Journal saved successfully");
    }

    // PUT /auth/journals/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Journal> updateJournal(@PathVariable Long id, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                journalService.updateJournal(id, request.get("title"), request.get("content"))
        );
    }

    // DELETE /auth/journals/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJournal(@PathVariable Long id) {
        journalService.deleteJournal(id);
        return ResponseEntity.ok("Journal deleted successfully");
    }
}
