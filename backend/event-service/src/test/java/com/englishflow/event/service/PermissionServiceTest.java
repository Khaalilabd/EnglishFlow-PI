package com.englishflow.event.service;

import com.englishflow.event.client.ClubServiceClient;
import com.englishflow.event.dto.MemberDTO;
import com.englishflow.event.enums.RankType;
import com.englishflow.event.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private ClubServiceClient clubServiceClient;

    @InjectMocks
    private PermissionService permissionService;

    @Test
    void checkEventCreationPermission_WhenUserIsPresident_ShouldPass() {
        // Arrange
        Long userId = 1L;
        MemberDTO member = new MemberDTO();
        member.setUserId(userId);
        member.setClubId(1);
        member.setRank(RankType.PRESIDENT);
        
        when(clubServiceClient.getMembersByUserId(userId)).thenReturn(Arrays.asList(member));

        // Act & Assert
        assertDoesNotThrow(() -> permissionService.checkEventCreationPermission(userId));
        verify(clubServiceClient).getMembersByUserId(userId);
    }

    @Test
    void checkEventCreationPermission_WhenUserIsVicePresident_ShouldPass() {
        // Arrange
        Long userId = 1L;
        MemberDTO member = new MemberDTO();
        member.setUserId(userId);
        member.setClubId(1);
        member.setRank(RankType.VICE_PRESIDENT);
        
        when(clubServiceClient.getMembersByUserId(userId)).thenReturn(Arrays.asList(member));

        // Act & Assert
        assertDoesNotThrow(() -> permissionService.checkEventCreationPermission(userId));
    }

    @Test
    void checkEventCreationPermission_WhenUserIsEventManager_ShouldPass() {
        // Arrange
        Long userId = 1L;
        MemberDTO member = new MemberDTO();
        member.setUserId(userId);
        member.setClubId(1);
        member.setRank(RankType.EVENT_MANAGER);
        
        when(clubServiceClient.getMembersByUserId(userId)).thenReturn(Arrays.asList(member));

        // Act & Assert
        assertDoesNotThrow(() -> permissionService.checkEventCreationPermission(userId));
    }

    @Test
    void checkEventCreationPermission_WhenUserIsRegularMember_ShouldThrowException() {
        // Arrange
        Long userId = 1L;
        MemberDTO member = new MemberDTO();
        member.setUserId(userId);
        member.setClubId(1);
        member.setRank(RankType.MEMBER);
        
        when(clubServiceClient.getMembersByUserId(userId)).thenReturn(Arrays.asList(member));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> 
            permissionService.checkEventCreationPermission(userId)
        );
    }

    @Test
    void checkEventCreationPermission_WhenUserNotInAnyClub_ShouldThrowException() {
        // Arrange
        Long userId = 1L;
        when(clubServiceClient.getMembersByUserId(userId)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> 
            permissionService.checkEventCreationPermission(userId)
        );
    }

    @Test
    void checkEventCreationPermission_WhenMembershipsNull_ShouldThrowException() {
        // Arrange
        Long userId = 1L;
        when(clubServiceClient.getMembersByUserId(userId)).thenReturn(null);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> 
            permissionService.checkEventCreationPermission(userId)
        );
    }

    @Test
    void checkEventCreationPermission_WhenServiceThrowsException_ShouldThrowUnauthorizedException() {
        // Arrange
        Long userId = 1L;
        when(clubServiceClient.getMembersByUserId(userId)).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> 
            permissionService.checkEventCreationPermission(userId)
        );
    }
}
