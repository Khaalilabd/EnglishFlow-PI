package com.englishflow.club.service;

import com.englishflow.club.dto.ClubDTO;
import com.englishflow.club.entity.Club;
import com.englishflow.club.enums.ClubCategory;
import com.englishflow.club.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubService {
    
    private final ClubRepository clubRepository;
    
    @Transactional(readOnly = true)
    public List<ClubDTO> getAllClubs() {
        return clubRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ClubDTO> getClubsByCategory(ClubCategory category) {
        return clubRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ClubDTO> searchClubsByName(String name) {
        return clubRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ClubDTO getClubById(Integer id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + id));
        return convertToDTO(club);
    }
    
    @Transactional
    public ClubDTO createClub(ClubDTO clubDTO) {
        Club club = convertToEntity(clubDTO);
        Club savedClub = clubRepository.save(club);
        return convertToDTO(savedClub);
    }
    
    @Transactional
    public ClubDTO updateClub(Integer id, ClubDTO clubDTO) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + id));
        
        club.setName(clubDTO.getName());
        club.setDescription(clubDTO.getDescription());
        club.setObjective(clubDTO.getObjective());
        club.setCategory(clubDTO.getCategory());
        club.setMaxMembers(clubDTO.getMaxMembers());
        
        // Update image if provided
        if (clubDTO.getImage() != null && !clubDTO.getImage().isEmpty()) {
            club.setImage(clubDTO.getImage());
        }
        
        Club updatedClub = clubRepository.save(club);
        return convertToDTO(updatedClub);
    }
    
    @Transactional
    public void deleteClub(Integer id) {
        if (!clubRepository.existsById(id)) {
            throw new RuntimeException("Club not found with id: " + id);
        }
        clubRepository.deleteById(id);
    }
    
    private ClubDTO convertToDTO(Club club) {
        return ClubDTO.builder()
                .id(club.getId())
                .name(club.getName())
                .description(club.getDescription())
                .objective(club.getObjective())
                .category(club.getCategory())
                .maxMembers(club.getMaxMembers())
                .image(club.getImage())
                .build();
    }
    
    private Club convertToEntity(ClubDTO clubDTO) {
        return Club.builder()
                .name(clubDTO.getName())
                .description(clubDTO.getDescription())
                .objective(clubDTO.getObjective())
                .category(clubDTO.getCategory())
                .maxMembers(clubDTO.getMaxMembers())
                .image(clubDTO.getImage())
                .build();
    }
}
