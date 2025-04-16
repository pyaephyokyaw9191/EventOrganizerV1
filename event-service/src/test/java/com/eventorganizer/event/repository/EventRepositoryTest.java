package com.eventorganizer.event.repository;

import com.eventorganizer.event.config.TestConfig;
import com.eventorganizer.event.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestConfig.class)
@ActiveProfiles("test")
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    private Event testEvent;

    @BeforeEach
    void setUp() {
        // Create a test event before each test
        testEvent = new Event();
        testEvent.setTitle("Test Event");
        testEvent.setDescription("Test Description");
        testEvent.setDateTime(LocalDateTime.now().plusDays(1));
        testEvent.setLocation("Test Location");
        testEvent.setCategory("CONFERENCE");
        testEvent.setPrice(99.99);
        testEvent.setOrganizerId(1L);
        testEvent.setStatus("ACTIVE");
        
        // Save the test event
        testEvent = eventRepository.save(testEvent);
    }

    @Test
    void findById_WhenEventExists_ReturnsEvent() {
        // Act
        Optional<Event> foundEvent = eventRepository.findById(testEvent.getId());

        // Assert
        assertTrue(foundEvent.isPresent());
        assertEquals(testEvent.getTitle(), foundEvent.get().getTitle());
        assertEquals(testEvent.getDescription(), foundEvent.get().getDescription());
        assertEquals(testEvent.getLocation(), foundEvent.get().getLocation());
        assertEquals(testEvent.getCategory(), foundEvent.get().getCategory());
        assertEquals(testEvent.getPrice(), foundEvent.get().getPrice());
        assertEquals(testEvent.getOrganizerId(), foundEvent.get().getOrganizerId());
        assertEquals(testEvent.getStatus(), foundEvent.get().getStatus());
    }

    @Test
    void findById_WhenEventDoesNotExist_ReturnsEmpty() {
        // Act
        Optional<Event> foundEvent = eventRepository.findById(999L);

        // Assert
        assertFalse(foundEvent.isPresent());
    }

    @Test
    void findByCategory_WhenEventsExist_ReturnsEvents() {
        // Arrange
        Event workshopEvent = new Event();
        workshopEvent.setTitle("Workshop Event");
        workshopEvent.setDescription("Workshop Description");
        workshopEvent.setDateTime(LocalDateTime.now().plusDays(2));
        workshopEvent.setLocation("Workshop Location");
        workshopEvent.setCategory("WORKSHOP");
        workshopEvent.setPrice(149.99);
        workshopEvent.setOrganizerId(1L);
        workshopEvent.setStatus("ACTIVE");
        eventRepository.save(workshopEvent);

        // Act
        List<Event> conferenceEvents = eventRepository.findByCategory("CONFERENCE");
        List<Event> workshopEvents = eventRepository.findByCategory("WORKSHOP");

        // Assert
        assertEquals(1, conferenceEvents.size());
        assertEquals(1, workshopEvents.size());
        assertEquals("Test Event", conferenceEvents.get(0).getTitle());
        assertEquals("Workshop Event", workshopEvents.get(0).getTitle());
    }

    @Test
    void findByOrganizerId_WhenEventsExist_ReturnsEvents() {
        // Arrange
        Event secondEvent = new Event();
        secondEvent.setTitle("Second Event");
        secondEvent.setDescription("Second Description");
        secondEvent.setDateTime(LocalDateTime.now().plusDays(3));
        secondEvent.setLocation("Second Location");
        secondEvent.setCategory("CONFERENCE");
        secondEvent.setPrice(199.99);
        secondEvent.setOrganizerId(1L);
        secondEvent.setStatus("ACTIVE");
        eventRepository.save(secondEvent);

        // Act
        List<Event> organizerEvents = eventRepository.findByOrganizerId(1L);

        // Assert
        assertEquals(2, organizerEvents.size());
    }

    @Test
    void findByStatus_WhenEventsExist_ReturnsEvents() {
        // Arrange
        Event cancelledEvent = new Event();
        cancelledEvent.setTitle("Cancelled Event");
        cancelledEvent.setDescription("Cancelled Description");
        cancelledEvent.setDateTime(LocalDateTime.now().plusDays(4));
        cancelledEvent.setLocation("Cancelled Location");
        cancelledEvent.setCategory("CONFERENCE");
        cancelledEvent.setPrice(299.99);
        cancelledEvent.setOrganizerId(1L);
        cancelledEvent.setStatus("CANCELLED");
        eventRepository.save(cancelledEvent);

        // Act
        List<Event> activeEvents = eventRepository.findByStatus("ACTIVE");
        List<Event> cancelledEvents = eventRepository.findByStatus("CANCELLED");

        // Assert
        assertEquals(1, activeEvents.size());
        assertEquals(1, cancelledEvents.size());
        assertEquals("Test Event", activeEvents.get(0).getTitle());
        assertEquals("Cancelled Event", cancelledEvents.get(0).getTitle());
    }

    @Test
    void findByDateTimeGreaterThanEqual_WhenFutureEventsExist_ReturnsEvents() {
        // Arrange
        Event pastEvent = new Event();
        pastEvent.setTitle("Past Event");
        pastEvent.setDescription("Past Description");
        pastEvent.setDateTime(LocalDateTime.now().plusDays(1));
        pastEvent.setLocation("Past Location");
        pastEvent.setCategory("CONFERENCE");
        pastEvent.setPrice(399.99);
        pastEvent.setOrganizerId(1L);
        pastEvent.setStatus("ACTIVE");
        eventRepository.save(pastEvent);

        // Act
        List<Event> futureEvents = eventRepository.findByDateTimeGreaterThanEqual(LocalDateTime.now());

        // Assert
        assertEquals(2, futureEvents.size());
        assertTrue(futureEvents.stream().anyMatch(e -> e.getTitle().equals("Test Event")));
        assertTrue(futureEvents.stream().anyMatch(e -> e.getTitle().equals("Past Event")));
    }
} 