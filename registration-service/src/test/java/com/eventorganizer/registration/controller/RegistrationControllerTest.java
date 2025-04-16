package com.eventorganizer.registration.controller;

import com.eventorganizer.registration.RegistrationServiceApplication;
import com.eventorganizer.registration.config.TestSecurityConfig;
import com.eventorganizer.registration.model.Registration;
import com.eventorganizer.registration.model.RegistrationStatus;
import com.eventorganizer.registration.service.RegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
@ContextConfiguration(classes = RegistrationServiceApplication.class)
@Import(TestSecurityConfig.class)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegistrationService registrationService;

    private Registration testRegistration;

    @BeforeEach
    void setUp() {
        testRegistration = new Registration();
        testRegistration.setId(1L);
        testRegistration.setEventId(1L);
        testRegistration.setUserId(1L);
        testRegistration.setRegistrationDate(LocalDateTime.now());
        testRegistration.setStatus(RegistrationStatus.REGISTERED);
        testRegistration.setTicketToken("test-token-123");
    }

    @Test
    void registerForEvent_ValidRequest_ShouldReturnCreated() throws Exception {
        when(registrationService.registerForEvent(eq(1L), eq(1L))).thenReturn(testRegistration);

        mockMvc.perform(post("/api/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegistrationRequest(1L, 1L))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testRegistration.getId()))
                .andExpect(jsonPath("$.eventId").value(testRegistration.getEventId()))
                .andExpect(jsonPath("$.userId").value(testRegistration.getUserId()))
                .andExpect(jsonPath("$.status").value(testRegistration.getStatus().toString()))
                .andExpect(jsonPath("$.ticketToken").value(testRegistration.getTicketToken()));
    }

    @Test
    void registerForEvent_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        when(registrationService.registerForEvent(eq(1L), eq(1L)))
                .thenThrow(new IllegalArgumentException("Invalid registration request"));

        mockMvc.perform(post("/api/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegistrationRequest(1L, 1L))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRegistrationsByUserId_ExistingRegistrations_ShouldReturnRegistrations() throws Exception {
        when(registrationService.getRegistrationsByUserId(1L))
                .thenReturn(Arrays.asList(testRegistration));

        mockMvc.perform(get("/api/registrations")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testRegistration.getId()))
                .andExpect(jsonPath("$[0].eventId").value(testRegistration.getEventId()))
                .andExpect(jsonPath("$[0].userId").value(testRegistration.getUserId()))
                .andExpect(jsonPath("$[0].status").value(testRegistration.getStatus().toString()))
                .andExpect(jsonPath("$[0].ticketToken").value(testRegistration.getTicketToken()));
    }

    @Test
    void getRegistrationsByUserId_NoRegistrations_ShouldReturnEmptyList() throws Exception {
        when(registrationService.getRegistrationsByUserId(1L))
                .thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/registrations")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void cancelRegistration_ValidRequest_ShouldReturnOk() throws Exception {
        testRegistration.setStatus(RegistrationStatus.CANCELLED);
        when(registrationService.cancelRegistration(eq(1L), eq(1L))).thenReturn(testRegistration);

        mockMvc.perform(delete("/api/registrations/{id}", 1L)
                .header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testRegistration.getId()))
                .andExpect(jsonPath("$.status").value(RegistrationStatus.CANCELLED.toString()));
    }

    @Test
    void cancelRegistration_Unauthorized_ShouldReturnForbidden() throws Exception {
        when(registrationService.cancelRegistration(eq(1L), eq(1L)))
                .thenThrow(new IllegalArgumentException("Not authorized"));

        mockMvc.perform(delete("/api/registrations/{id}", 1L)
                .header("X-User-Id", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void cancelRegistration_NonExistent_ShouldReturnNotFound() throws Exception {
        when(registrationService.cancelRegistration(eq(1L), eq(1L)))
                .thenThrow(new IllegalArgumentException("Registration not found"));

        mockMvc.perform(delete("/api/registrations/{id}", 1L)
                .header("X-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRegistrationById_ExistingRegistration_ShouldReturnRegistration() throws Exception {
        when(registrationService.getRegistrationById(1L)).thenReturn(testRegistration);

        mockMvc.perform(get("/api/registrations/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testRegistration.getId()))
                .andExpect(jsonPath("$.eventId").value(testRegistration.getEventId()))
                .andExpect(jsonPath("$.userId").value(testRegistration.getUserId()))
                .andExpect(jsonPath("$.status").value(testRegistration.getStatus().toString()))
                .andExpect(jsonPath("$.ticketToken").value(testRegistration.getTicketToken()));
    }

    @Test
    void getRegistrationById_NonExistent_ShouldReturnNotFound() throws Exception {
        when(registrationService.getRegistrationById(1L))
                .thenThrow(new IllegalArgumentException("Registration not found"));

        mockMvc.perform(get("/api/registrations/{id}", 1L))
                .andExpect(status().isNotFound());
    }
} 