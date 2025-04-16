package com.eventorganizer.user.controller;

import com.eventorganizer.user.model.UserProfile;
import com.eventorganizer.user.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
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
    void getUserProfile_WhenUserExists_ReturnsUserProfile() throws Exception {
        // Arrange
        when(userProfileService.getUserProfile(TEST_USERNAME)).thenReturn(userProfile);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{username}", TEST_USERNAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(userProfileService).getUserProfile(TEST_USERNAME);
    }

    @Test
    void getUserProfile_WhenUserDoesNotExist_ReturnsNotFound() throws Exception {
        // Arrange
        when(userProfileService.getUserProfile(TEST_USERNAME))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{username}", TEST_USERNAME))
                .andExpect(status().isNotFound());

        verify(userProfileService).getUserProfile(TEST_USERNAME);
    }

    @Test
    void updateUserProfile_WhenUserExists_UpdatesProfile() throws Exception {
        // Arrange
        UserProfile updatedProfile = UserProfile.builder()
                .username(TEST_USERNAME)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phone("0987654321")
                .bio("Updated bio")
                .build();

        when(userProfileService.updateUserProfile(eq(TEST_USERNAME), any(UserProfile.class)))
                .thenReturn(updatedProfile);

        // Act & Assert
        mockMvc.perform(put("/api/v1/users/{username}", TEST_USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"firstName\":\"Jane\",\"lastName\":\"Smith\",\"email\":\"jane.smith@example.com\",\"phone\":\"0987654321\",\"bio\":\"Updated bio\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"))
                .andExpect(jsonPath("$.phone").value("0987654321"))
                .andExpect(jsonPath("$.bio").value("Updated bio"));

        verify(userProfileService).updateUserProfile(eq(TEST_USERNAME), any(UserProfile.class));
    }

    @Test
    void updateUserProfile_WhenUserDoesNotExist_ReturnsNotFound() throws Exception {
        // Arrange
        when(userProfileService.updateUserProfile(eq(TEST_USERNAME), any(UserProfile.class)))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        mockMvc.perform(put("/api/v1/users/{username}", TEST_USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"firstName\":\"Jane\",\"lastName\":\"Smith\",\"email\":\"jane.smith@example.com\"}"))
                .andExpect(status().isNotFound());

        verify(userProfileService).updateUserProfile(eq(TEST_USERNAME), any(UserProfile.class));
    }

    @Test
    void createUserProfile_WithValidData_Success() throws Exception {
        // Arrange
        when(userProfileService.createUserProfile(any(UserProfile.class)))
                .thenReturn(userProfile);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\",\"phone\":\"1234567890\",\"bio\":\"Test bio\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andExpect(jsonPath("$.bio").value("Test bio"));

        verify(userProfileService).createUserProfile(any(UserProfile.class));
    }

    @Test
    void createUserProfile_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Arrange
        when(userProfileService.createUserProfile(any(UserProfile.class)))
                .thenThrow(new RuntimeException("Invalid data"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\"}"))
                .andExpect(status().isBadRequest());

        verify(userProfileService).createUserProfile(any(UserProfile.class));
    }

    @Test
    void deleteUserProfile_WhenUserExists_Success() throws Exception {
        // Arrange
        doNothing().when(userProfileService).deleteUserProfile(TEST_USERNAME);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/users/{username}", TEST_USERNAME))
                .andExpect(status().isOk());

        verify(userProfileService).deleteUserProfile(TEST_USERNAME);
    }

    @Test
    void deleteUserProfile_WhenUserDoesNotExist_ReturnsNotFound() throws Exception {
        // Arrange
        doThrow(new RuntimeException("User not found"))
                .when(userProfileService).deleteUserProfile(TEST_USERNAME);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/users/{username}", TEST_USERNAME))
                .andExpect(status().isNotFound());

        verify(userProfileService).deleteUserProfile(TEST_USERNAME);
    }
} 