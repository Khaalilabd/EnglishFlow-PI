package com.englishflow.courses.service;

import com.englishflow.courses.client.MessagingServiceClient;
import com.englishflow.courses.dto.PackDTO;
import com.englishflow.courses.entity.Pack;
import com.englishflow.courses.enums.PackStatus;
import com.englishflow.courses.repository.PackEnrollmentRepository;
import com.englishflow.courses.repository.PackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PackServiceTest {

    @Mock
    private PackRepository packRepository;

    @Mock
    private PackEnrollmentRepository enrollmentRepository;

    @Mock
    private MessagingServiceClient messagingServiceClient;

    @InjectMocks
    private PackService packService;

    private Pack testPack;
    private PackDTO testPackDTO;

    @BeforeEach
    void setUp() {
        testPack = new Pack();
        testPack.setId(1L);
        testPack.setName("Test Pack");
        testPack.setCategory("English");
        testPack.setLevel("Beginner");
        testPack.setTutorId(1L);
        testPack.setTutorName("Test Tutor");
        testPack.setTutorRating(4.5);
        testPack.setCourseIds(Arrays.asList(1L, 2L, 3L));
        testPack.setPrice(BigDecimal.valueOf(200.0));
        testPack.setEstimatedDuration(120);
        testPack.setMaxStudents(30);
        testPack.setCurrentEnrolledStudents(10);
        testPack.setEnrollmentStartDate(LocalDateTime.now());
        testPack.setEnrollmentEndDate(LocalDateTime.now().plusMonths(1));
        testPack.setDescription("Test Description");
        testPack.setStatus(PackStatus.ACTIVE);
        testPack.setCreatedBy(1L);
        testPack.setConversationId(100L);

        testPackDTO = new PackDTO();
        testPackDTO.setId(1L);
        testPackDTO.setName("Test Pack");
        testPackDTO.setCategory("English");
        testPackDTO.setLevel("Beginner");
        testPackDTO.setTutorId(1L);
        testPackDTO.setTutorName("Test Tutor");
        testPackDTO.setTutorRating(4.5);
        testPackDTO.setCourseIds(Arrays.asList(1L, 2L, 3L));
        testPackDTO.setPrice(BigDecimal.valueOf(200.0));
        testPackDTO.setEstimatedDuration(120);
        testPackDTO.setMaxStudents(30);
        testPackDTO.setEnrollmentStartDate(LocalDateTime.now());
        testPackDTO.setEnrollmentEndDate(LocalDateTime.now().plusMonths(1));
        testPackDTO.setDescription("Test Description");
        testPackDTO.setStatus(PackStatus.ACTIVE);
        testPackDTO.setCreatedBy(1L);
    }

    @Test
    void createPack_WithValidData_ShouldCreatePack() {
        when(packRepository.save(any(Pack.class))).thenReturn(testPack);
        when(messagingServiceClient.createPackGroup(anyString(), anyString(), anyLong(), anyList())).thenReturn(100L);

        PackDTO result = packService.createPack(testPackDTO);

        assertNotNull(result);
        assertEquals("Test Pack", result.getName());
        verify(packRepository, times(2)).save(any(Pack.class));
        verify(messagingServiceClient, times(1)).createPackGroup(anyString(), anyString(), anyLong(), anyList());
    }

    @Test
    void createPack_WhenMessagingServiceFails_ShouldStillCreatePack() {
        when(packRepository.save(any(Pack.class))).thenReturn(testPack);
        when(messagingServiceClient.createPackGroup(anyString(), anyString(), anyLong(), anyList()))
                .thenThrow(new RuntimeException("Messaging service error"));

        PackDTO result = packService.createPack(testPackDTO);

        assertNotNull(result);
        assertEquals("Test Pack", result.getName());
        verify(packRepository, times(1)).save(any(Pack.class));
    }

    @Test
    void updatePack_WhenPackExists_ShouldUpdatePack() {
        testPackDTO.setName("Updated Pack");
        when(packRepository.findById(1L)).thenReturn(Optional.of(testPack));
        when(packRepository.save(any(Pack.class))).thenReturn(testPack);

        PackDTO result = packService.updatePack(1L, testPackDTO);

        assertNotNull(result);
        verify(packRepository, times(1)).findById(1L);
        verify(packRepository, times(1)).save(any(Pack.class));
    }

    @Test
    void updatePack_WhenPackNotExists_ShouldThrowException() {
        when(packRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> packService.updatePack(999L, testPackDTO));
        verify(packRepository, times(1)).findById(999L);
        verify(packRepository, never()).save(any(Pack.class));
    }

    @Test
    void getById_WhenPackExists_ShouldReturnPack() {
        when(packRepository.findById(1L)).thenReturn(Optional.of(testPack));

        PackDTO result = packService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Pack", result.getName());
        verify(packRepository, times(1)).findById(1L);
    }

    @Test
    void getById_WhenPackNotExists_ShouldThrowException() {
        when(packRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> packService.getById(999L));
        verify(packRepository, times(1)).findById(999L);
    }

    @Test
    void getAllPacks_ShouldReturnAllPacks() {
        List<Pack> packs = Arrays.asList(testPack);
        when(packRepository.findAll()).thenReturn(packs);

        List<PackDTO> result = packService.getAllPacks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Pack", result.get(0).getName());
        verify(packRepository, times(1)).findAll();
    }

    @Test
    void getByTutorId_ShouldReturnTutorPacks() {
        List<Pack> packs = Arrays.asList(testPack);
        when(packRepository.findByTutorId(1L)).thenReturn(packs);

        List<PackDTO> result = packService.getByTutorId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getTutorId());
        verify(packRepository, times(1)).findByTutorId(1L);
    }

    @Test
    void getByStatus_ShouldReturnPacksWithStatus() {
        List<Pack> packs = Arrays.asList(testPack);
        when(packRepository.findByStatus(PackStatus.ACTIVE)).thenReturn(packs);

        List<PackDTO> result = packService.getByStatus(PackStatus.ACTIVE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(PackStatus.ACTIVE, result.get(0).getStatus());
        verify(packRepository, times(1)).findByStatus(PackStatus.ACTIVE);
    }

    @Test
    void getByCategoryAndLevel_ShouldReturnFilteredPacks() {
        List<Pack> packs = Arrays.asList(testPack);
        when(packRepository.findByCategoryAndLevel("English", "Beginner")).thenReturn(packs);

        List<PackDTO> result = packService.getByCategoryAndLevel("English", "Beginner");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("English", result.get(0).getCategory());
        assertEquals("Beginner", result.get(0).getLevel());
        verify(packRepository, times(1)).findByCategoryAndLevel("English", "Beginner");
    }

    @Test
    void getAvailablePacksByCategoryAndLevel_ShouldReturnAvailablePacks() {
        List<Pack> packs = Arrays.asList(testPack);
        when(packRepository.findAvailablePacksByCategoryAndLevel("English", "Beginner")).thenReturn(packs);

        List<PackDTO> result = packService.getAvailablePacksByCategoryAndLevel("English", "Beginner");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(packRepository, times(1)).findAvailablePacksByCategoryAndLevel("English", "Beginner");
    }

    @Test
    void getAllAvailablePacks_ShouldReturnAllAvailablePacks() {
        List<Pack> packs = Arrays.asList(testPack);
        when(packRepository.findAllAvailablePacks()).thenReturn(packs);

        List<PackDTO> result = packService.getAllAvailablePacks();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(packRepository, times(1)).findAllAvailablePacks();
    }

    @Test
    void getByCreatedBy_ShouldReturnPacksCreatedByAcademic() {
        List<Pack> packs = Arrays.asList(testPack);
        when(packRepository.findByCreatedBy(1L)).thenReturn(packs);

        List<PackDTO> result = packService.getByCreatedBy(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCreatedBy());
        verify(packRepository, times(1)).findByCreatedBy(1L);
    }

    @Test
    void deletePack_WhenPackExists_ShouldDeletePackAndEnrollments() {
        when(packRepository.findById(1L)).thenReturn(Optional.of(testPack));
        when(messagingServiceClient.deletePackGroup(anyLong())).thenReturn(true);
        doNothing().when(enrollmentRepository).deleteByPackId(1L);
        doNothing().when(packRepository).deleteById(1L);

        packService.deletePack(1L);

        verify(packRepository, times(1)).findById(1L);
        verify(messagingServiceClient, times(1)).deletePackGroup(100L);
        verify(enrollmentRepository, times(1)).deleteByPackId(1L);
        verify(packRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletePack_WhenMessagingServiceFails_ShouldStillDeletePack() {
        when(packRepository.findById(1L)).thenReturn(Optional.of(testPack));
        when(messagingServiceClient.deletePackGroup(anyLong())).thenThrow(new RuntimeException("Messaging service error"));
        doNothing().when(enrollmentRepository).deleteByPackId(1L);
        doNothing().when(packRepository).deleteById(1L);

        packService.deletePack(1L);

        verify(enrollmentRepository, times(1)).deleteByPackId(1L);
        verify(packRepository, times(1)).deleteById(1L);
    }

    @Test
    void incrementEnrollment_ShouldIncreaseEnrollmentCount() {
        when(packRepository.findById(1L)).thenReturn(Optional.of(testPack));
        when(packRepository.save(any(Pack.class))).thenReturn(testPack);

        packService.incrementEnrollment(1L);

        verify(packRepository, times(1)).findById(1L);
        verify(packRepository, times(1)).save(any(Pack.class));
    }

    @Test
    void incrementEnrollment_WhenPackBecomesFull_ShouldUpdateStatus() {
        testPack.setCurrentEnrolledStudents(29);
        testPack.setMaxStudents(30);
        when(packRepository.findById(1L)).thenReturn(Optional.of(testPack));
        when(packRepository.save(any(Pack.class))).thenReturn(testPack);

        packService.incrementEnrollment(1L);

        verify(packRepository, times(1)).save(any(Pack.class));
    }

    @Test
    void decrementEnrollment_ShouldDecreaseEnrollmentCount() {
        when(packRepository.findById(1L)).thenReturn(Optional.of(testPack));
        when(packRepository.save(any(Pack.class))).thenReturn(testPack);

        packService.decrementEnrollment(1L);

        verify(packRepository, times(1)).findById(1L);
        verify(packRepository, times(1)).save(any(Pack.class));
    }

    @Test
    void decrementEnrollment_WhenPackWasFull_ShouldUpdateStatus() {
        testPack.setCurrentEnrolledStudents(30);
        testPack.setMaxStudents(30);
        testPack.setStatus(PackStatus.FULL);
        when(packRepository.findById(1L)).thenReturn(Optional.of(testPack));
        when(packRepository.save(any(Pack.class))).thenReturn(testPack);

        packService.decrementEnrollment(1L);

        verify(packRepository, times(1)).save(any(Pack.class));
    }

    @Test
    void decrementEnrollment_WhenCountIsZero_ShouldNotDecrement() {
        testPack.setCurrentEnrolledStudents(0);
        when(packRepository.findById(1L)).thenReturn(Optional.of(testPack));

        packService.decrementEnrollment(1L);

        verify(packRepository, times(1)).findById(1L);
        verify(packRepository, never()).save(any(Pack.class));
    }
}
