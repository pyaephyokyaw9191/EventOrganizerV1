package com.eventorganizer.registration.service;

import com.eventorganizer.registration.model.Registration;
import com.eventorganizer.registration.model.RegistrationStatus;
import com.eventorganizer.registration.repository.RegistrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
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
    void registerForEvent_ValidRegistration_ShouldSucceed() {
        // Mock event service response
        when(restTemplate.getForObject(anyString(), eq(Boolean.class))).thenReturn(true);
        
        // Mock repository save
        when(registrationRepository.save(any(Registration.class))).thenReturn(testRegistration);
        
        Registration result = registrationService.registerForEvent(1L, 1L);
        
        assertNotNull(result);
        assertEquals(1L, result.getEventId());
        assertEquals(1L, result.getUserId());
        assertEquals(RegistrationStatus.REGISTERED, result.getStatus());
        assertNotNull(result.getTicketToken());
        
        verify(registrationRepository).save(any(Registration.class));
    }

    @Test
    void registerForEvent_NonExistentEvent_ShouldThrowException() {
        // Mock event service response
        when(restTemplate.getForObject(anyString(), eq(Boolean.class))).thenReturn(false);
        
        assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEvent(1L, 1L);
        });
        
        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    void getRegistrationsByUserId_ExistingRegistrations_ShouldReturnRegistrations() {
        List<Registration> expectedRegistrations = Arrays.asList(testRegistration);
        when(registrationRepository.findByUserId(1L)).thenReturn(expectedRegistrations);
        
        List<Registration> result = registrationService.getRegistrationsByUserId(1L);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
    }

    @Test
    void getRegistrationsByUserId_NoRegistrations_ShouldReturnEmptyList() {
        when(registrationRepository.findByUserId(1L)).thenReturn(Arrays.asList());
        
        List<Registration> result = registrationService.getRegistrationsByUserId(1L);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void cancelRegistration_ExistingRegistration_ShouldSucceed() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(testRegistration));
        when(registrationRepository.save(any(Registration.class))).thenReturn(testRegistration);
        
        Registration result = registrationService.cancelRegistration(1L, 1L);
        
        assertNotNull(result);
        assertEquals(RegistrationStatus.CANCELLED, result.getStatus());
        
        verify(registrationRepository).save(any(Registration.class));
    }

    @Test
    void cancelRegistration_NonExistentRegistration_ShouldThrowException() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () -> {
            registrationService.cancelRegistration(1L, 1L);
        });
        
        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    void cancelRegistration_UnauthorizedUser_ShouldThrowException() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(testRegistration));
        
        assertThrows(IllegalArgumentException.class, () -> {
            registrationService.cancelRegistration(1L, 2L); // Different user ID
        });
        
        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    void cancelRegistration_AlreadyCancelled_ShouldThrowException() {
        testRegistration.setStatus(RegistrationStatus.CANCELLED);
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(testRegistration));
        
        assertThrows(IllegalArgumentException.class, () -> {
            registrationService.cancelRegistration(1L, 1L);
        });
        
        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    void getRegistrationById_ExistingRegistration_ShouldReturnRegistration() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(testRegistration));
        
        Registration result = registrationService.getRegistrationById(1L);
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getEventId());
        assertEquals(1L, result.getUserId());
    }

    @Test
    void getRegistrationById_NonExistentRegistration_ShouldThrowException() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () -> {
            registrationService.getRegistrationById(1L);
        });
    }
} 