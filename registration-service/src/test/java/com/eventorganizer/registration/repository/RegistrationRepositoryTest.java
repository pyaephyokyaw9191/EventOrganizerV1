package com.eventorganizer.registration.repository;

import com.eventorganizer.registration.RegistrationServiceApplication;
import com.eventorganizer.registration.model.Registration;
import com.eventorganizer.registration.model.RegistrationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = RegistrationServiceApplication.class)
@ActiveProfiles("test")
class RegistrationRepositoryTest {

    @Autowired
    private RegistrationRepository registrationRepository;

    private Registration testRegistration;

    @BeforeEach
    void setUp() {
        registrationRepository.deleteAll();
        
        testRegistration = new Registration();
        testRegistration.setEventId(1L);
        testRegistration.setUserId(1L);
        testRegistration.setRegistrationDate(LocalDateTime.now());
        testRegistration.setStatus(RegistrationStatus.REGISTERED);
        testRegistration.setTicketToken("test-token-123");
        
        testRegistration = registrationRepository.save(testRegistration);
    }

    @Test
    void save_ValidRegistration_ShouldSucceed() {
        Registration newRegistration = new Registration();
        newRegistration.setEventId(2L);
        newRegistration.setUserId(2L);
        newRegistration.setRegistrationDate(LocalDateTime.now());
        newRegistration.setStatus(RegistrationStatus.REGISTERED);
        newRegistration.setTicketToken("new-token-456");
        
        Registration savedRegistration = registrationRepository.save(newRegistration);
        
        assertNotNull(savedRegistration.getId());
        assertEquals(newRegistration.getEventId(), savedRegistration.getEventId());
        assertEquals(newRegistration.getUserId(), savedRegistration.getUserId());
        assertEquals(newRegistration.getStatus(), savedRegistration.getStatus());
        assertEquals(newRegistration.getTicketToken(), savedRegistration.getTicketToken());
    }

    @Test
    void findById_ExistingRegistration_ShouldReturnRegistration() {
        Optional<Registration> foundRegistration = registrationRepository.findById(testRegistration.getId());
        
        assertTrue(foundRegistration.isPresent());
        assertEquals(testRegistration.getId(), foundRegistration.get().getId());
        assertEquals(testRegistration.getEventId(), foundRegistration.get().getEventId());
        assertEquals(testRegistration.getUserId(), foundRegistration.get().getUserId());
        assertEquals(testRegistration.getStatus(), foundRegistration.get().getStatus());
        assertEquals(testRegistration.getTicketToken(), foundRegistration.get().getTicketToken());
    }

    @Test
    void findById_NonExistingRegistration_ShouldReturnEmpty() {
        Optional<Registration> foundRegistration = registrationRepository.findById(999L);
        
        assertFalse(foundRegistration.isPresent());
    }

    @Test
    void findByUserId_ExistingRegistrations_ShouldReturnRegistrations() {
        // Create another registration for the same user
        Registration anotherRegistration = new Registration();
        anotherRegistration.setEventId(3L);
        anotherRegistration.setUserId(1L); // Same user ID
        anotherRegistration.setRegistrationDate(LocalDateTime.now());
        anotherRegistration.setStatus(RegistrationStatus.REGISTERED);
        anotherRegistration.setTicketToken("another-token-789");
        registrationRepository.save(anotherRegistration);
        
        List<Registration> userRegistrations = registrationRepository.findByUserId(1L);
        
        assertEquals(2, userRegistrations.size());
        assertTrue(userRegistrations.stream().allMatch(r -> r.getUserId().equals(1L)));
    }

    @Test
    void findByUserId_NonExistingRegistrations_ShouldReturnEmptyList() {
        List<Registration> userRegistrations = registrationRepository.findByUserId(999L);
        
        assertTrue(userRegistrations.isEmpty());
    }

    @Test
    void findByEventId_ExistingRegistrations_ShouldReturnRegistrations() {
        // Create another registration for the same event
        Registration anotherRegistration = new Registration();
        anotherRegistration.setEventId(1L); // Same event ID
        anotherRegistration.setUserId(2L);
        anotherRegistration.setRegistrationDate(LocalDateTime.now());
        anotherRegistration.setStatus(RegistrationStatus.REGISTERED);
        anotherRegistration.setTicketToken("another-token-789");
        registrationRepository.save(anotherRegistration);
        
        List<Registration> eventRegistrations = registrationRepository.findByEventId(1L);
        
        assertEquals(2, eventRegistrations.size());
        assertTrue(eventRegistrations.stream().allMatch(r -> r.getEventId().equals(1L)));
    }

    @Test
    void findByEventId_NonExistingRegistrations_ShouldReturnEmptyList() {
        List<Registration> eventRegistrations = registrationRepository.findByEventId(999L);
        
        assertTrue(eventRegistrations.isEmpty());
    }

    @Test
    void findByStatus_ExistingRegistrations_ShouldReturnRegistrations() {
        // Create a cancelled registration
        Registration cancelledRegistration = new Registration();
        cancelledRegistration.setEventId(3L);
        cancelledRegistration.setUserId(3L);
        cancelledRegistration.setRegistrationDate(LocalDateTime.now());
        cancelledRegistration.setStatus(RegistrationStatus.CANCELLED);
        cancelledRegistration.setTicketToken("cancelled-token-789");
        registrationRepository.save(cancelledRegistration);
        
        List<Registration> registeredRegistrations = registrationRepository.findByStatus(RegistrationStatus.REGISTERED);
        List<Registration> cancelledRegistrations = registrationRepository.findByStatus(RegistrationStatus.CANCELLED);
        
        assertEquals(1, registeredRegistrations.size());
        assertEquals(1, cancelledRegistrations.size());
        assertTrue(registeredRegistrations.stream().allMatch(r -> r.getStatus() == RegistrationStatus.REGISTERED));
        assertTrue(cancelledRegistrations.stream().allMatch(r -> r.getStatus() == RegistrationStatus.CANCELLED));
    }

    @Test
    void findByStatus_NonExistingRegistrations_ShouldReturnEmptyList() {
        List<Registration> registrations = registrationRepository.findByStatus(RegistrationStatus.CANCELLED);
        
        assertTrue(registrations.isEmpty());
    }
} 