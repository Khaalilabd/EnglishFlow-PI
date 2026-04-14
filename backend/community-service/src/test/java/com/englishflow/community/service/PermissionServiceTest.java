package com.englishflow.community.service;

import com.englishflow.community.entity.Category;
import com.englishflow.community.entity.SubCategory;
import com.englishflow.community.exception.ResourceNotFoundException;
import com.englishflow.community.repository.CategoryRepository;
import com.englishflow.community.repository.SubCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private SubCategoryRepository subCategoryRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private PermissionService permissionService;

    private SubCategory subCategory;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("General");
        category.setIsLocked(false);

        subCategory = new SubCategory();
        subCategory.setId(1L);
        subCategory.setName("Announcements");
        subCategory.setCategory(category);
        subCategory.setIsLocked(false);
        subCategory.setRequiresAdminRole(false);
        subCategory.setRequiresClubMembership(false);
    }

    @Test
    void canCreateTopic_NormalSubCategory_ShouldReturnTrue() {
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));

        boolean result = permissionService.canCreateTopic(1L, "STUDENT");

        assertTrue(result);
        verify(subCategoryRepository).findById(1L);
    }

    @Test
    void canCreateTopic_RequiresAdmin_StudentRole_ShouldReturnFalse() {
        subCategory.setRequiresAdminRole(true);
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));

        boolean result = permissionService.canCreateTopic(1L, "STUDENT");

        assertFalse(result);
    }

    @Test
    void canCreateTopic_RequiresAdmin_AdminRole_ShouldReturnTrue() {
        subCategory.setRequiresAdminRole(true);
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));

        boolean result = permissionService.canCreateTopic(1L, "ADMIN");

        assertTrue(result);
    }

    @Test
    void canCreateTopic_RequiresAdmin_AcademicRole_ShouldReturnTrue() {
        subCategory.setRequiresAdminRole(true);
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));

        boolean result = permissionService.canCreateTopic(1L, "ACADEMIC_OFFICE_AFFAIR");

        assertTrue(result);
    }

    @Test
    void canCreateTopic_RequiresClubMembership_ShouldReturnFalse() {
        subCategory.setRequiresClubMembership(true);
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));

        boolean result = permissionService.canCreateTopic(1L, "STUDENT");

        assertFalse(result);
    }

    @Test
    void canCreateTopic_CategoryLocked_StudentRole_ShouldReturnFalse() {
        category.setIsLocked(true);
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));

        boolean result = permissionService.canCreateTopic(1L, "STUDENT");

        assertFalse(result);
    }

    @Test
    void canCreateTopic_CategoryLocked_AcademicRole_ShouldReturnTrue() {
        category.setIsLocked(true);
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));

        boolean result = permissionService.canCreateTopic(1L, "ACADEMIC_OFFICE_AFFAIR");

        assertTrue(result);
    }

    @Test
    void canCreateTopic_SubCategoryLocked_StudentRole_ShouldReturnFalse() {
        subCategory.setIsLocked(true);
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));

        boolean result = permissionService.canCreateTopic(1L, "STUDENT");

        assertFalse(result);
    }

    @Test
    void canCreateTopic_SubCategoryLocked_AcademicRole_ShouldReturnTrue() {
        subCategory.setIsLocked(true);
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));

        boolean result = permissionService.canCreateTopic(1L, "ACADEMIC_OFFICE_AFFAIR");

        assertTrue(result);
    }

    @Test
    void canCreateTopic_SubCategoryNotFound_ShouldThrowException() {
        when(subCategoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            permissionService.canCreateTopic(999L, "STUDENT");
        });
    }

    @Test
    void canReplyToTopic_NormalSubCategory_ShouldReturnTrue() {
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));

        boolean result = permissionService.canReplyToTopic(1L, "STUDENT");

        assertTrue(result);
    }

    @Test
    void canReplyToTopic_CategoryLocked_ShouldReturnFalse() {
        category.setIsLocked(true);
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));

        boolean result = permissionService.canReplyToTopic(1L, "STUDENT");

        assertFalse(result);
    }

    @Test
    void canReplyToTopic_CategoryLocked_AdminRole_ShouldReturnFalse() {
        category.setIsLocked(true);
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));

        boolean result = permissionService.canReplyToTopic(1L, "ADMIN");

        assertFalse(result);
    }

    @Test
    void canReplyToTopic_SubCategoryLocked_ShouldReturnFalse() {
        subCategory.setIsLocked(true);
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));

        boolean result = permissionService.canReplyToTopic(1L, "STUDENT");

        assertFalse(result);
    }

    @Test
    void getPermissionInfo_ShouldReturnCompleteInfo() {
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));

        PermissionService.PermissionInfo info = permissionService.getPermissionInfo(1L, "STUDENT");

        assertNotNull(info);
        assertTrue(info.canCreateTopic);
        assertTrue(info.canReply);
        assertFalse(info.categoryLocked);
        assertFalse(info.subCategoryLocked);
    }

    @Test
    void getPermissionInfo_LockedCategory_ShouldReturnRestrictedInfo() {
        category.setIsLocked(true);
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));

        PermissionService.PermissionInfo info = permissionService.getPermissionInfo(1L, "STUDENT");

        assertNotNull(info);
        assertFalse(info.canCreateTopic);
        assertFalse(info.canReply);
        assertTrue(info.categoryLocked);
        assertFalse(info.subCategoryLocked);
    }
}
