package com.eventorganizer.user.repository;

import com.eventorganizer.user.model.UserProfile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserProfileRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Test
    void findByUsername_WhenUserExists_ReturnsUserProfile() {
        // Arrange
        UserProfile userProfile = UserProfile.builder()
                .username("testuser")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        entityManager.persist(userProfile);
        entityManager.flush();

        // Act
        Optional<UserProfile> found = userProfileRepository.findByUsername("testuser");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        assertEquals("John", found.get().getFirstName());
        assertEquals("Doe", found.get().getLastName());
        assertEquals("john.doe@example.com", found.get().getEmail());
    }

    @Test
    void findByUsername_WhenUserDoesNotExist_ReturnsEmpty() {
        // Act
        Optional<UserProfile> found = userProfileRepository.findByUsername("nonexistent");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void findByEmail_WhenUserExists_ReturnsUserProfile() {
        // Arrange
        UserProfile userProfile = UserProfile.builder()
                .username("testuser")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        entityManager.persist(userProfile);
        entityManager.flush();

        // Act
        Optional<UserProfile> found = userProfileRepository.findByEmail("john.doe@example.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        assertEquals("john.doe@example.com", found.get().getEmail());
    }

    @Test
    void findByEmail_WhenUserDoesNotExist_ReturnsEmpty() {
        // Act
        Optional<UserProfile> found = userProfileRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void existsByUsername_WhenUserExists_ReturnsTrue() {
        // Arrange
        UserProfile userProfile = UserProfile.builder()
                .username("testuser")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        entityManager.persist(userProfile);
        entityManager.flush();

        // Act
        boolean exists = userProfileRepository.existsByUsername("testuser");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByUsername_WhenUserDoesNotExist_ReturnsFalse() {
        // Act
        boolean exists = userProfileRepository.existsByUsername("nonexistent");

        // Assert
        assertFalse(exists);
    }

    @Test
    void existsByEmail_WhenUserExists_ReturnsTrue() {
        // Arrange
        UserProfile userProfile = UserProfile.builder()
                .username("testuser")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        entityManager.persist(userProfile);
        entityManager.flush();

        // Act
        boolean exists = userProfileRepository.existsByEmail("john.doe@example.com");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByEmail_WhenUserDoesNotExist_ReturnsFalse() {
        // Act
        boolean exists = userProfileRepository.existsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(exists);
    }
} 