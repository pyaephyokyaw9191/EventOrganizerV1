package com.eventorganizer.user.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileTest {

    @Test
    void createUserProfile_WithValidData_Success() {
        // Arrange
        UserProfile userProfile = UserProfile.builder()
                .id(1L)
                .username("testuser")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .bio("Test bio")
                .profilePictureUrl("http://example.com/profile.jpg")
                .build();

        // Assert
        assertNotNull(userProfile);
        assertEquals(1L, userProfile.getId());
        assertEquals("testuser", userProfile.getUsername());
        assertEquals("John", userProfile.getFirstName());
        assertEquals("Doe", userProfile.getLastName());
        assertEquals("john.doe@example.com", userProfile.getEmail());
        assertEquals("1234567890", userProfile.getPhone());
        assertEquals("Test bio", userProfile.getBio());
        assertEquals("http://example.com/profile.jpg", userProfile.getProfilePictureUrl());
    }

    @Test
    void createUserProfile_WithMinimalData_Success() {
        // Arrange
        UserProfile userProfile = UserProfile.builder()
                .username("testuser")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        // Assert
        assertNotNull(userProfile);
        assertNull(userProfile.getId());
        assertEquals("testuser", userProfile.getUsername());
        assertEquals("John", userProfile.getFirstName());
        assertEquals("Doe", userProfile.getLastName());
        assertEquals("john.doe@example.com", userProfile.getEmail());
        assertNull(userProfile.getPhone());
        assertNull(userProfile.getBio());
        assertNull(userProfile.getProfilePictureUrl());
    }

    @Test
    void equalsAndHashCode_WithSameObjects_ReturnsTrue() {
        // Arrange
        UserProfile userProfile1 = UserProfile.builder()
                .id(1L)
                .username("testuser")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        UserProfile userProfile2 = UserProfile.builder()
                .id(1L)
                .username("testuser")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        // Assert
        assertEquals(userProfile1, userProfile2);
        assertEquals(userProfile1.hashCode(), userProfile2.hashCode());
    }

    @Test
    void equalsAndHashCode_WithDifferentObjects_ReturnsFalse() {
        // Arrange
        UserProfile userProfile1 = UserProfile.builder()
                .id(1L)
                .username("testuser1")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        UserProfile userProfile2 = UserProfile.builder()
                .id(2L)
                .username("testuser2")
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .build();

        // Assert
        assertNotEquals(userProfile1, userProfile2);
        assertNotEquals(userProfile1.hashCode(), userProfile2.hashCode());
    }
} 