package com.englishflow.sponsors.service;

import com.englishflow.sponsors.dto.SponsorDTO;
import com.englishflow.sponsors.entity.Sponsor;
import com.englishflow.sponsors.exception.SponsorNotFoundException;
import com.englishflow.sponsors.mapper.SponsorMapper;
import com.englishflow.sponsors.repository.SponsorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SponsorService {
    
    private final SponsorRepository sponsorRepository;
    private final SponsorMapper sponsorMapper;
    private final WebSocketNotificationService webSocketNotificationService;
    
    @Cacheable(value = "sponsors", key = "'all'")
    @Transactional(readOnly = true)
    public List<SponsorDTO> getAllSponsors() {
        log.info("Fetching all sponsors");
        return sponsorRepository.findAll().stream()
                .map(sponsorMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Cacheable(value = "sponsorById", key = "#id")
    @Transactional(readOnly = true)
    public SponsorDTO getSponsorById(Long id) {
        log.info("Fetching sponsor by id: {}", id);
        Sponsor sponsor = sponsorRepository.findById(id)
                .orElseThrow(() -> new SponsorNotFoundException(id));
        return sponsorMapper.toDTO(sponsor);
    }
    
    @Cacheable(value = "sponsorsByLevel", key = "#level")
    @Transactional(readOnly = true)
    public List<SponsorDTO> getSponsorsByLevel(Sponsor.SponsorLevel level) {
        log.info("Fetching sponsors by level: {}", level);
        return sponsorRepository.findByLevel(level).stream()
                .map(sponsorMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Cacheable(value = "sponsorsByStatus", key = "'approved'")
    @Transactional(readOnly = true)
    public List<SponsorDTO> getApprovedSponsors() {
        return sponsorRepository.findByStatus(Sponsor.SponsorStatus.APPROVED).stream()
                .map(sponsorMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "sponsorsByStatus", key = "'pending'")
    @Transactional(readOnly = true)
    public List<SponsorDTO> getPendingSponsors() {
        return sponsorRepository.findByStatus(Sponsor.SponsorStatus.PENDING).stream()
                .map(sponsorMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Caching(evict = {
        @CacheEvict(value = "sponsors", key = "'all'"),
        @CacheEvict(value = "sponsorsByStatus", allEntries = true)
    })
    @Transactional
    public SponsorDTO approveSponsor(Long id) {
        Sponsor sponsor = sponsorRepository.findById(id)
                .orElseThrow(() -> new SponsorNotFoundException(id));
        sponsor.setStatus(Sponsor.SponsorStatus.APPROVED);
        return sponsorMapper.toDTO(sponsorRepository.save(sponsor));
    }

    @Caching(evict = {
        @CacheEvict(value = "sponsors", key = "'all'"),
        @CacheEvict(value = "sponsorsByStatus", allEntries = true)
    })
    @Transactional
    public SponsorDTO rejectSponsor(Long id) {
        Sponsor sponsor = sponsorRepository.findById(id)
                .orElseThrow(() -> new SponsorNotFoundException(id));
        sponsor.setStatus(Sponsor.SponsorStatus.REJECTED);
        return sponsorMapper.toDTO(sponsorRepository.save(sponsor));
    }

    @Caching(evict = {
        @CacheEvict(value = "sponsors", key = "'all'"),
        @CacheEvict(value = "sponsorsByLevel", allEntries = true),
        @CacheEvict(value = "sponsorsByStatus", allEntries = true)
    })
    @Transactional
    public SponsorDTO createSponsor(SponsorDTO sponsorDTO) {
        log.info("Creating new sponsor: {} with contribution amount: {}", sponsorDTO.getName(), sponsorDTO.getContributionAmount());
        Sponsor sponsor = sponsorMapper.toEntity(sponsorDTO);
        Sponsor savedSponsor = sponsorRepository.save(sponsor);
        SponsorDTO result = sponsorMapper.toDTO(savedSponsor);
        
        log.info("Sponsor created successfully with id: {}, contribution: {}, level: {}", 
                savedSponsor.getId(), savedSponsor.getContributionAmount(), savedSponsor.getLevel());
        
        // Send WebSocket notification
        webSocketNotificationService.notifySponsorCreated(result);
        
        return result;
    }
    
    @Caching(evict = {
        @CacheEvict(value = "sponsors", key = "'all'"),
        @CacheEvict(value = "sponsorById", key = "#id"),
        @CacheEvict(value = "sponsorsByLevel", allEntries = true),
        @CacheEvict(value = "sponsorsByStatus", allEntries = true)
    })
    @Transactional
    public SponsorDTO updateSponsor(Long id, SponsorDTO sponsorDTO) {
        log.info("Updating sponsor with id: {} with contribution amount: {}", id, sponsorDTO.getContributionAmount());
        Sponsor existingSponsor = sponsorRepository.findById(id)
                .orElseThrow(() -> new SponsorNotFoundException(id));
        
        sponsorMapper.updateEntityFromDTO(sponsorDTO, existingSponsor);
        Sponsor updatedSponsor = sponsorRepository.save(existingSponsor);
        SponsorDTO result = sponsorMapper.toDTO(updatedSponsor);
        
        log.info("Sponsor updated successfully with contribution: {}, level: {}", 
                updatedSponsor.getContributionAmount(), updatedSponsor.getLevel());
        
        // Send WebSocket notification
        webSocketNotificationService.notifySponsorUpdated(result);
        
        return result;
    }
    
    @Caching(evict = {
        @CacheEvict(value = "sponsors", key = "'all'"),
        @CacheEvict(value = "sponsorById", key = "#id"),
        @CacheEvict(value = "sponsorsByLevel", allEntries = true),
        @CacheEvict(value = "sponsorsByStatus", allEntries = true)
    })
    @Transactional
    public void deleteSponsor(Long id) {
        log.info("Deleting sponsor with id: {}", id);
        Sponsor sponsor = sponsorRepository.findById(id)
                .orElseThrow(() -> new SponsorNotFoundException(id));
        
        String sponsorName = sponsor.getName();
        sponsorRepository.deleteById(id);
        
        log.info("Sponsor deleted successfully");
        
        // Send WebSocket notification
        webSocketNotificationService.notifySponsorDeleted(id, sponsorName);
    }
}
