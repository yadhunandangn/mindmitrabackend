package com.MindMitra.Rolebased.Service;

import com.MindMitra.Rolebased.Entity.Journal;
import com.MindMitra.Rolebased.Repository.JournalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JournalService {

    private final JournalRepository journalRepository;

    public List<Journal> getJournalsByUsername(String username) {
        return journalRepository.findByUsernameOrderByTimestampDesc(username);
    }

    public Journal createJournal(String username, String title, String content) {
        Journal journal = Journal.builder()
                .username(username)
                .title(title)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();
        return journalRepository.save(journal);
    }

    public Journal updateJournal(Long id, String title, String content) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Journal not found with id: " + id));
        journal.setTitle(title);
        journal.setContent(content);
        journal.setTimestamp(LocalDateTime.now());
        return journalRepository.save(journal);
    }

    public void deleteJournal(Long id) {
        if (!journalRepository.existsById(id)) {
            throw new RuntimeException("Journal not found with id: " + id);
        }
        journalRepository.deleteById(id);
    }
}
