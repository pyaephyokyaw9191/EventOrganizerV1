package com.eventorganizer.user.service;

import com.eventorganizer.user.model.UserProfile;
import com.eventorganizer.user.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    private UserProfile userProfile;
    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        userProfile = UserProfile.builder()
                .id(1L)
                .username(TEST_USERNAME)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .bio("Test bio")
                .build();
    }

    @Test
    void getUserProfile_WhenUserExists_ReturnsUserProfile() {
        // Arrange
        when(userProfileRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(userProfile));

        // Act
        UserProfile found = userProfileService.getUserProfile(TEST_USERNAME);

        // Assert
        assertNotNull(found);
        assertEquals(TEST_USERNAME, found.getUsername());
        assertEquals("John", found.getFirstName());
        assertEquals("Doe", found.getLastName());
        assertEquals("john.doe@example.com", found.getEmail());
        verify(userProfileRepository).findByUsername(TEST_USERNAME);
    }

    @Test
    void getUserProfile_WhenUserDoesNotExist_ThrowsException() {
        // Arrange
        when(userProfileRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userProfileService.getUserProfile(TEST_USERNAME));
        verify(userProfileRepository).findByUsername(TEST_USERNAME);
    }

    @Test
    void updateUserProfile_WhenUserExists_UpdatesProfile() {
        // Arrange
        UserProfile updatedProfile = UserProfile.builder()
                .username(TEST_USERNAME)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phone("0987654321")
                .bio("Updated bio")
                .build();

        when(userProfileRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(userProfile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(updatedProfile);

        // Act
        UserProfile result = userProfileService.updateUserProfile(TEST_USERNAME, updatedProfile);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_USERNAME, result.getUsername());
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("jane.smith@example.com", result.getEmail());
        assertEquals("0987654321", result.getPhone());
        assertEquals("Updated bio", result.getBio());
        verify(userProfileRepository).findByUsername(TEST_USERNAME);
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void updateUserProfile_WhenUserDoesNotExist_ThrowsException() {
        // Arrange
        UserProfile updatedProfile = UserProfile.builder()
                .username(TEST_USERNAME)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .build();

        when(userProfileRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userProfileService.updateUserProfile(TEST_USERNAME, updatedProfile));
        verify(userProfileRepository).findByUsername(TEST_USERNAME);
        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }

    @Test
    void createUserProfile_WithValidData_Success() {
        // Arrange
        when(userProfileRepository.existsByUsername(TEST_USERNAME)).thenReturn(false);
        when(userProfileRepository.existsByEmail(userProfile.getEmail())).thenReturn(false);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);

        // Act
        UserProfile result = userProfileService.createUserProfile(userProfile);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_USERNAME, result.getUsername());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userProfileRepository).existsByUsername(TEST_USERNAME);
        verify(userProfileRepository).existsByEmail(userProfile.getEmail());
        verify(userProfileRepository).save(userProfile);
    }

    @Test
    void createUserProfile_WhenUsernameExists_ThrowsException() {
        // Arrange
        when(userProfileRepository.existsByUsername(TEST_USERNAME)).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userProfileService.createUserProfile(userProfile));
        verify(userProfileRepository).existsByUsername(TEST_USERNAME);
        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }

    @Test
    void createUserProfile_WhenEmailExists_ThrowsException() {
        // Arrange
        when(userProfileRepository.existsByUsername(TEST_USERNAME)).thenReturn(false);
        when(userProfileRepository.existsByEmail(userProfile.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userProfileService.createUserProfile(userProfile));
        verify(userProfileRepository).existsByUsername(TEST_USERNAME);
        verify(userProfileRepository).existsByEmail(userProfile.getEmail());
        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }

    @Test
    void deleteUserProfile_WhenUserExists_Success() {
        // Arrange
        when(userProfileRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(userProfile));
        doNothing().when(userProfileRepository).delete(userProfile);

        // Act
        userProfileService.deleteUserProfile(TEST_USERNAME);

        // Assert
        verify(userProfileRepository).findByUsername(TEST_USERNAME);
        verify(userProfileRepository).delete(userProfile);
    }

    @Test
    void deleteUserProfile_WhenUserDoesNotExist_ThrowsException() {
        // Arrange
        when(userProfileRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userProfileService.deleteUserProfile(TEST_USERNAME));
        verify(userProfileRepository).findByUsername(TEST_USERNAME);
        verify(userProfileRepository, never()).delete(any(UserProfile.class));
    }
} 