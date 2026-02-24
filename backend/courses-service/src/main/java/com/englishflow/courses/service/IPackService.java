package com.englishflow.courses.service;

import com.englishflow.courses.dto.PackDTO;
import com.englishflow.courses.enums.PackStatus;

import java.util.List;

public interface IPackService {
    
    PackDTO createPack(PackDTO packDTO);
    
    PackDTO updatePack(Long id, PackDTO packDTO);
    
    PackDTO getById(Long id);
    
    List<PackDTO> getAllPacks();
    
    List<PackDTO> getByTutorId(Long tutorId);
    
    List<PackDTO> getByStatus(PackStatus status);
    
    List<PackDTO> getByCategoryAndLevel(String category, String level);
    
    List<PackDTO> getAvailablePacksByCategoryAndLevel(String category, String level);
    
    List<PackDTO> getAllAvailablePacks();
    
    List<PackDTO> getByCreatedBy(Long academicId);
    
    void deletePack(Long id);
    
    void incrementEnrollment(Long packId);
    
    void decrementEnrollment(Long packId);
}
