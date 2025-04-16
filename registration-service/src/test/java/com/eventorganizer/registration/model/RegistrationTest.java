package com.eventorganizer.registration.model;

import com.eventorganizer.registration.RegistrationServiceApplication;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RegistrationTest {

    private Validator validator;
    private Registration registration;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        registration = new Registration();
        registration.setEventId(1L);
        registration.setUserId(1L);
        registration.setRegistrationDate(LocalDateTime.now());
        registration.setStatus(RegistrationStatus.REGISTERED);
        registration.setTicketToken("test-token-123");
    }

    @Test
    void createRegistration_WithValidData_ShouldSucceed() {
        var violations = validator.validate(registration);
        assertTrue(violations.isEmpty());
    }

    @Test
    void createRegistration_WithNullEventId_ShouldFail() {
        registration.setEventId(null);
        var violations = validator.validate(registration);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Event ID is required", violations.iterator().next().getMessage());
    }

    @Test
    void createRegistration_WithNullUserId_ShouldFail() {
        registration.setUserId(null);
        var violations = validator.validate(registration);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("User ID is required", violations.iterator().next().getMessage());
    }

    @Test
    void createRegistration_WithNullRegistrationDate_ShouldFail() {
        registration.setRegistrationDate(null);
        var violations = validator.validate(registration);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Registration date is required", violations.iterator().next().getMessage());
    }

    @Test
    void createRegistration_WithNullStatus_ShouldFail() {
        registration.setStatus(null);
        var violations = validator.validate(registration);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Status is required", violations.iterator().next().getMessage());
    }

    @Test
    void createRegistration_WithNullTicketToken_ShouldFail() {
        registration.setTicketToken(null);
        var violations = validator.validate(registration);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Ticket token is required", violations.iterator().next().getMessage());
    }

    @Test
    void createRegistration_WithEmptyTicketToken_ShouldFail() {
        registration.setTicketToken("");
        var violations = validator.validate(registration);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Ticket token is required", violations.iterator().next().getMessage());
    }
} 