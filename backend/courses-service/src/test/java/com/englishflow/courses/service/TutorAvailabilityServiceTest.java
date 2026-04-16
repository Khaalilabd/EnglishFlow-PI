package com.englishflow.courses.service;

import com.englishflow.courses.dto.TimeSlotDTO;
import com.englishflow.courses.dto.TutorAvailabilityDTO;
import com.englishflow.courses.entity.TimeSlot;
import com.englishflow.courses.entity.TutorAvailability;
import com.englishflow.courses.enums.DayOfWeek;
import com.englishflow.courses.enums.TutorStatus;
import com.englishflow.courses.repository.TutorAvailabilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutorAvailabilityServiceTest {

    @Mock
    private TutorAvailabilityRepository availabilityRepository;

    @InjectMocks
    private TutorAvailabilityService tutorAvailabilityService;

    private TutorAvailability testAvailability;
    private TutorAvailabilityDTO testAvailabilityDTO;
    private TimeSlot testTimeSlot;
    private TimeSlotDTO testTimeSlotDTO;

    @BeforeEach
    void setUp() {
        testTimeSlot = new TimeSlot();
        testTimeSlot.setId(1L);
        testTimeSlot.setStartTime(LocalTime.of(9, 0));
        testTimeSlot.setEndTime(LocalTime.of(17, 0));

        testAvailability = new TutorAvailability();
        testAvailability.setId(1L);
        testAvailability.setTutorId(1L);
        testAvailability.setTutorName("Test Tutor");
        testAvailability.setAvailableDays(new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY)));
        testAvailability.setMaxStudentsCapacity(20);
        testAvailability.setCurrentStudentsCount(10);
        testAvailability.setCategories(new HashSet<>(Arrays.asList("English", "Math")));
        testAvailability.setLevels(new HashSet<>(Arrays.asList("Beginner", "Intermediate")));
        testAvailability.setStatus(TutorStatus.AVAILABLE);
        testAvailability.setCreatedAt(LocalDateTime.now());
        testAvailability.setLastUpdated(LocalDateTime.now());
        
        List<TimeSlot> timeSlots = new ArrayList<>();
        testTimeSlot.setTutorAvailability(testAvailability);
        timeSlots.add(testTimeSlot);
        testAvailability.setTimeSlots(timeSlots);

        testTimeSlotDTO = new TimeSlotDTO();
        testTimeSlotDTO.setId(1L);
        testTimeSlotDTO.setStartTime(LocalTime.of(9, 0));
        testTimeSlotDTO.setEndTime(LocalTime.of(17, 0));

        testAvailabilityDTO = new TutorAvailabilityDTO();
        testAvailabilityDTO.setId(1L);
        testAvailabilityDTO.setTutorId(1L);
        testAvailabilityDTO.setTutorName("Test Tutor");
        testAvailabilityDTO.setAvailableDays(new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY)));
        testAvailabilityDTO.setMaxStudentsCapacity(20);
        testAvailabilityDTO.setCategories(new HashSet<>(Arrays.asList("English", "Math")));
        testAvailabilityDTO.setLevels(new HashSet<>(Arrays.asList("Beginner", "Intermediate")));
        testAvailabilityDTO.setStatus(TutorStatus.AVAILABLE);
        testAvailabilityDTO.setTimeSlots(Arrays.asList(testTimeSlotDTO));
    }

    @Test
    void createOrUpdateAvailability_WhenNew_ShouldCreateAvailability() {
        when(availabilityRepository.findByTutorId(1L)).thenReturn(Optional.empty());
        when(availabilityRepository.save(any(TutorAvailability.class))).thenReturn(testAvailability);

        TutorAvailabilityDTO result = tutorAvailabilityService.createOrUpdateAvailability(testAvailabilityDTO);

        assertNotNull(result);
        assertEquals("Test Tutor", result.getTutorName());
        verify(availabilityRepository, times(1)).findByTutorId(1L);
        verify(availabilityRepository, times(1)).save(any(TutorAvailability.class));
    }

    @Test
    void createOrUpdateAvailability_WhenExists_ShouldUpdateAvailability() {
        when(availabilityRepository.findByTutorId(1L)).thenReturn(Optional.of(testAvailability));
        when(availabilityRepository.save(any(TutorAvailability.class))).thenReturn(testAvailability);

        TutorAvailabilityDTO result = tutorAvailabilityService.createOrUpdateAvailability(testAvailabilityDTO);

        assertNotNull(result);
        verify(availabilityRepository, times(1)).findByTutorId(1L);
        verify(availabilityRepository, times(1)).save(any(TutorAvailability.class));
    }

    @Test
    void getByTutorId_WhenAvailabilityExists_ShouldReturnAvailability() {
        when(availabilityRepository.findByTutorId(1L)).thenReturn(Optional.of(testAvailability));

        TutorAvailabilityDTO result = tutorAvailabilityService.getByTutorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getTutorId());
        assertEquals("Test Tutor", result.getTutorName());
        verify(availabilityRepository, times(1)).findByTutorId(1L);
    }

    @Test
    void getByTutorId_WhenAvailabilityNotExists_ShouldReturnNull() {
        when(availabilityRepository.findByTutorId(anyLong())).thenReturn(Optional.empty());

        TutorAvailabilityDTO result = tutorAvailabilityService.getByTutorId(999L);

        assertNull(result);
        verify(availabilityRepository, times(1)).findByTutorId(999L);
    }

    @Test
    void getById_WhenAvailabilityExists_ShouldReturnAvailability() {
        when(availabilityRepository.findById(1L)).thenReturn(Optional.of(testAvailability));

        TutorAvailabilityDTO result = tutorAvailabilityService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(availabilityRepository, times(1)).findById(1L);
    }

    @Test
    void getById_WhenAvailabilityNotExists_ShouldThrowException() {
        when(availabilityRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tutorAvailabilityService.getById(999L));
        verify(availabilityRepository, times(1)).findById(999L);
    }

    @Test
    void getAllAvailabilities_ShouldReturnAllAvailabilities() {
        List<TutorAvailability> availabilities = Arrays.asList(testAvailability);
        when(availabilityRepository.findAll()).thenReturn(availabilities);

        List<TutorAvailabilityDTO> result = tutorAvailabilityService.getAllAvailabilities();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(availabilityRepository, times(1)).findAll();
    }

    @Test
    void getByStatus_ShouldReturnAvailabilitiesWithStatus() {
        List<TutorAvailability> availabilities = Arrays.asList(testAvailability);
        when(availabilityRepository.findByStatus(TutorStatus.AVAILABLE)).thenReturn(availabilities);

        List<TutorAvailabilityDTO> result = tutorAvailabilityService.getByStatus(TutorStatus.AVAILABLE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TutorStatus.AVAILABLE, result.get(0).getStatus());
        verify(availabilityRepository, times(1)).findByStatus(TutorStatus.AVAILABLE);
    }

    @Test
    void getAvailableTutorsByCategoryAndLevel_ShouldReturnFilteredTutors() {
        List<TutorAvailability> availabilities = Arrays.asList(testAvailability);
        when(availabilityRepository.findAvailableTutorsByCategoryAndLevel("English", "Beginner"))
                .thenReturn(availabilities);

        List<TutorAvailabilityDTO> result = tutorAvailabilityService
                .getAvailableTutorsByCategoryAndLevel("English", "Beginner");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(availabilityRepository, times(1))
                .findAvailableTutorsByCategoryAndLevel("English", "Beginner");
    }

    @Test
    void getTutorsWithCapacity_ShouldReturnTutorsWithAvailableCapacity() {
        List<TutorAvailability> availabilities = Arrays.asList(testAvailability);
        when(availabilityRepository.findTutorsWithCapacity()).thenReturn(availabilities);

        List<TutorAvailabilityDTO> result = tutorAvailabilityService.getTutorsWithCapacity();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(availabilityRepository, times(1)).findTutorsWithCapacity();
    }

    @Test
    void incrementStudentCount_ShouldIncreaseCount() {
        when(availabilityRepository.findByTutorId(1L)).thenReturn(Optional.of(testAvailability));
        when(availabilityRepository.save(any(TutorAvailability.class))).thenReturn(testAvailability);

        tutorAvailabilityService.incrementStudentCount(1L);

        verify(availabilityRepository, times(1)).findByTutorId(1L);
        verify(availabilityRepository, times(1)).save(any(TutorAvailability.class));
    }

    @Test
    void incrementStudentCount_WhenReachingCapacity_ShouldUpdateStatusToBusy() {
        testAvailability.setCurrentStudentsCount(19);
        testAvailability.setMaxStudentsCapacity(20);
        when(availabilityRepository.findByTutorId(1L)).thenReturn(Optional.of(testAvailability));
        when(availabilityRepository.save(any(TutorAvailability.class))).thenReturn(testAvailability);

        tutorAvailabilityService.incrementStudentCount(1L);

        verify(availabilityRepository, times(1)).save(any(TutorAvailability.class));
    }

    @Test
    void incrementStudentCount_WhenTutorNotFound_ShouldThrowException() {
        when(availabilityRepository.findByTutorId(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tutorAvailabilityService.incrementStudentCount(999L));
        verify(availabilityRepository, times(1)).findByTutorId(999L);
        verify(availabilityRepository, never()).save(any(TutorAvailability.class));
    }

    @Test
    void decrementStudentCount_ShouldDecreaseCount() {
        when(availabilityRepository.findByTutorId(1L)).thenReturn(Optional.of(testAvailability));
        when(availabilityRepository.save(any(TutorAvailability.class))).thenReturn(testAvailability);

        tutorAvailabilityService.decrementStudentCount(1L);

        verify(availabilityRepository, times(1)).findByTutorId(1L);
        verify(availabilityRepository, times(1)).save(any(TutorAvailability.class));
    }

    @Test
    void decrementStudentCount_WhenBusyAndBelowCapacity_ShouldUpdateStatusToAvailable() {
        testAvailability.setCurrentStudentsCount(20);
        testAvailability.setMaxStudentsCapacity(20);
        testAvailability.setStatus(TutorStatus.BUSY);
        when(availabilityRepository.findByTutorId(1L)).thenReturn(Optional.of(testAvailability));
        when(availabilityRepository.save(any(TutorAvailability.class))).thenReturn(testAvailability);

        tutorAvailabilityService.decrementStudentCount(1L);

        verify(availabilityRepository, times(1)).save(any(TutorAvailability.class));
    }

    @Test
    void decrementStudentCount_WhenCountIsZero_ShouldNotDecrement() {
        testAvailability.setCurrentStudentsCount(0);
        when(availabilityRepository.findByTutorId(1L)).thenReturn(Optional.of(testAvailability));

        tutorAvailabilityService.decrementStudentCount(1L);

        verify(availabilityRepository, times(1)).findByTutorId(1L);
        verify(availabilityRepository, never()).save(any(TutorAvailability.class));
    }

    @Test
    void decrementStudentCount_WhenTutorNotFound_ShouldThrowException() {
        when(availabilityRepository.findByTutorId(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tutorAvailabilityService.decrementStudentCount(999L));
        verify(availabilityRepository, times(1)).findByTutorId(999L);
        verify(availabilityRepository, never()).save(any(TutorAvailability.class));
    }

    @Test
    void deleteAvailability_ShouldDeleteAvailability() {
        doNothing().when(availabilityRepository).deleteById(1L);

        tutorAvailabilityService.deleteAvailability(1L);

        verify(availabilityRepository, times(1)).deleteById(1L);
    }
}
