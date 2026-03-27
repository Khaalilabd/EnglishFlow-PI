package com.jungle.learning.controller;

import com.jungle.learning.dto.NoteDTO;
import com.jungle.learning.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<NoteDTO> createNote(@RequestBody Map<String, Object> request) {
        Long progressId = ((Number) request.get("progressId")).longValue();
        Integer pageNumber = (Integer) request.get("pageNumber");
        String content = (String) request.get("content");
        String highlightedText = (String) request.get("highlightedText");
        String color = (String) request.get("color");
        
        NoteDTO note = noteService.createNote(progressId, pageNumber, content, highlightedText, color);
        return ResponseEntity.ok(note);
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<NoteDTO> updateNote(
            @PathVariable Long noteId,
            @RequestBody Map<String, String> request) {
        String content = request.get("content");
        NoteDTO note = noteService.updateNote(noteId, content);
        return ResponseEntity.ok(note);
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long noteId) {
        noteService.deleteNote(noteId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/progress/{progressId}")
    public ResponseEntity<List<NoteDTO>> getProgressNotes(@PathVariable Long progressId) {
        List<NoteDTO> notes = noteService.getProgressNotes(progressId);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/progress/{progressId}/page/{pageNumber}")
    public ResponseEntity<List<NoteDTO>> getPageNotes(
            @PathVariable Long progressId,
            @PathVariable Integer pageNumber) {
        List<NoteDTO> notes = noteService.getPageNotes(progressId, pageNumber);
        return ResponseEntity.ok(notes);
    }
}
