package com.jungle.learning.service;

import com.jungle.learning.dto.NoteDTO;
import com.jungle.learning.model.Note;
import com.jungle.learning.model.ReadingProgress;
import com.jungle.learning.repository.NoteRepository;
import com.jungle.learning.repository.ReadingProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final ReadingProgressRepository progressRepository;

    @Transactional
    public NoteDTO createNote(Long progressId, Integer pageNumber, String content, 
                              String highlightedText, String color) {
        ReadingProgress progress = progressRepository.findById(progressId)
                .orElseThrow(() -> new RuntimeException("Reading progress not found"));

        Note note = new Note();
        note.setProgress(progress);
        note.setPageNumber(pageNumber);
        note.setContent(content);
        note.setHighlightedText(highlightedText);
        note.setColor(color != null ? color : "#FFEB3B");

        Note saved = noteRepository.save(note);
        return mapToDTO(saved);
    }

    @Transactional
    public NoteDTO updateNote(Long noteId, String content) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        note.setContent(content);
        Note updated = noteRepository.save(note);
        return mapToDTO(updated);
    }

    @Transactional
    public void deleteNote(Long noteId) {
        noteRepository.deleteById(noteId);
    }

    public List<NoteDTO> getProgressNotes(Long progressId) {
        return noteRepository.findByProgressIdOrderByPageNumberAsc(progressId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<NoteDTO> getPageNotes(Long progressId, Integer pageNumber) {
        return noteRepository.findByProgressIdAndPageNumber(progressId, pageNumber)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private NoteDTO mapToDTO(Note note) {
        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setProgressId(note.getProgress().getId());
        dto.setPageNumber(note.getPageNumber());
        dto.setContent(note.getContent());
        dto.setHighlightedText(note.getHighlightedText());
        dto.setColor(note.getColor());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());
        return dto;
    }
}
