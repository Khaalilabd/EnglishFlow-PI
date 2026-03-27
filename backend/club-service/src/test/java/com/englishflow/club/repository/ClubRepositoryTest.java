package com.englishflow.club.repository;

import com.englishflow.club.entity.Club;
import com.englishflow.club.enums.ClubCategory;
import com.englishflow.club.enums.ClubStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ClubRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClubRepository clubRepository;

    private Club testClub;

    @BeforeEach
    void setUp() {
        testClub = Club.builder()
                .name("English Conversation Club")
                .description("Practice English conversation")
                .objective("Improve speaking skills")
                .category(ClubCategory.CONVERSATION)
                .maxMembers(20)
                .status(ClubStatus.APPROVED)
                .createdBy(1)
                .build();
    }

    @Test
    void findByCategory_ShouldReturnClubsWithMatchingCategory() {
        // Arrange
        entityManager.persist(testClub);
        entityManager.flush();

        // Act
        List<Club> result = clubRepository.findByCategory(ClubCategory.CONVERSATION);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(ClubCategory.CONVERSATION, result.get(0).getCategory());
    }

    @Test
    void findByNameContainingIgnoreCase_ShouldReturnMatchingClubs() {
        // Arrange
        entityManager.persist(testClub);
        entityManager.flush();

        // Act
        List<Club> result = clubRepository.findByNameContainingIgnoreCase("conversation");

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getName().toLowerCase().contains("conversation"));
    }

    @Test
    void findByStatus_ShouldReturnClubsWithMatchingStatus() {
        // Arrange
        entityManager.persist(testClub);
        entityManager.flush();

        // Act
        List<Club> result = clubRepository.findByStatus(ClubStatus.APPROVED);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(ClubStatus.APPROVED, result.get(0).getStatus());
    }

    @Test
    void findByCreatedBy_ShouldReturnClubsCreatedByUser() {
        // Arrange
        entityManager.persist(testClub);
        entityManager.flush();

        // Act
        List<Club> result = clubRepository.findByCreatedBy(1);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.get(0).getCreatedBy());
    }

    @Test
    void save_ShouldPersistClub() {
        // Act
        Club savedClub = clubRepository.save(testClub);

        // Assert
        assertNotNull(savedClub.getId());
        assertNotNull(savedClub.getCreatedAt());
        assertNotNull(savedClub.getUpdatedAt());
        assertEquals("English Conversation Club", savedClub.getName());
    }
}
