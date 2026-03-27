package com.englishflow.courses.controller;

import com.englishflow.courses.dto.PackDTO;
import com.englishflow.courses.enums.PackStatus;
import com.englishflow.courses.service.IPackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/packs")
@RequiredArgsConstructor
public class PackController {
    
    private final IPackService packService;
    
    @PostMapping
    public ResponseEntity<PackDTO> createPack(@RequestBody PackDTO packDTO) {
        PackDTO created = packService.createPack(packDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PackDTO> updatePack(@PathVariable Long id, @RequestBody PackDTO packDTO) {
        PackDTO updated = packService.updatePack(id, packDTO);
        return ResponseEntity.ok(updated);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PackDTO> getById(@PathVariable Long id) {
        PackDTO pack = packService.getById(id);
        return ResponseEntity.ok(pack);
    }
    
    @GetMapping
    public ResponseEntity<List<PackDTO>> getAllPacks() {
        List<PackDTO> packs = packService.getAllPacks();
        return ResponseEntity.ok(packs);
    }
    
    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<List<PackDTO>> getByTutorId(@PathVariable Long tutorId) {
        List<PackDTO> packs = packService.getByTutorId(tutorId);
        return ResponseEntity.ok(packs);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PackDTO>> getByStatus(@PathVariable PackStatus status) {
        List<PackDTO> packs = packService.getByStatus(status);
        return ResponseEntity.ok(packs);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<PackDTO>> searchPacks(
            @RequestParam String category,
            @RequestParam String level) {
        List<PackDTO> packs = packService.getByCategoryAndLevel(category, level);
        return ResponseEntity.ok(packs);
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<PackDTO>> getAvailablePacks(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String level) {
        List<PackDTO> packs;
        if (category != null && level != null) {
            packs = packService.getAvailablePacksByCategoryAndLevel(category, level);
        } else {
            packs = packService.getAllAvailablePacks();
        }
        return ResponseEntity.ok(packs);
    }
    
    @GetMapping("/academic/{academicId}")
    public ResponseEntity<List<PackDTO>> getByCreatedBy(@PathVariable Long academicId) {
        List<PackDTO> packs = packService.getByCreatedBy(academicId);
        return ResponseEntity.ok(packs);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePack(@PathVariable Long id) {
        packService.deletePack(id);
        return ResponseEntity.noContent().build();
    }
}
