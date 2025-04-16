package com.eventorganizer.event.model;

import com.eventorganizer.event.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestConfig.class)
@ActiveProfiles("test")
class EventTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testEventPersistence() {
        // Create a new event
        Event event = new Event();
        event.setTitle("Test Event");
        event.setDescription("Test Description");
        event.setDateTime(LocalDateTime.now().plusDays(1));
        event.setLocation("Test Location");
        event.setCategory("CONFERENCE");
        event.setPrice(99.99);
        event.setOrganizerId(1L);
        event.setStatus("ACTIVE");

        // Persist the event
        entityManager.persist(event);
        entityManager.flush();
        entityManager.clear();

        // Retrieve the event
        Event foundEvent = entityManager.find(Event.class, event.getId());

        // Assert the event was persisted correctly
        assertNotNull(foundEvent);
        assertEquals("Test Event", foundEvent.getTitle());
        assertEquals("Test Description", foundEvent.getDescription());
        assertEquals("Test Location", foundEvent.getLocation());
        assertEquals("CONFERENCE", foundEvent.getCategory());
        assertEquals(99.99, foundEvent.getPrice());
        assertEquals(1L, foundEvent.getOrganizerId());
        assertEquals("ACTIVE", foundEvent.getStatus());
    }

    @Test
    void testEventUpdate() {
        // Create and persist an event
        Event event = new Event();
        event.setTitle("Original Title");
        event.setDescription("Original Description");
        event.setDateTime(LocalDateTime.now().plusDays(1));
        event.setLocation("Original Location");
        event.setCategory("CONFERENCE");
        event.setPrice(99.99);
        event.setOrganizerId(1L);
        event.setStatus("ACTIVE");

        entityManager.persist(event);
        entityManager.flush();
        entityManager.clear();

        // Update the event
        Event foundEvent = entityManager.find(Event.class, event.getId());
        foundEvent.setTitle("Updated Title");
        foundEvent.setDescription("Updated Description");
        foundEvent.setPrice(149.99);
        
        entityManager.merge(foundEvent);
        entityManager.flush();
        entityManager.clear();

        // Retrieve the updated event
        Event updatedEvent = entityManager.find(Event.class, event.getId());

        // Assert the event was updated correctly
        assertNotNull(updatedEvent);
        assertEquals("Updated Title", updatedEvent.getTitle());
        assertEquals("Updated Description", updatedEvent.getDescription());
        assertEquals(149.99, updatedEvent.getPrice());
    }

    @Test
    void testEventDeletion() {
        // Create and persist an event
        Event event = new Event();
        event.setTitle("Event to Delete");
        event.setDescription("Description");
        event.setDateTime(LocalDateTime.now().plusDays(1));
        event.setLocation("Location");
        event.setCategory("CONFERENCE");
        event.setPrice(99.99);
        event.setOrganizerId(1L);
        event.setStatus("ACTIVE");

        entityManager.persist(event);
        entityManager.flush();
        entityManager.clear();

        // Delete the event
        Event foundEvent = entityManager.find(Event.class, event.getId());
        entityManager.remove(foundEvent);
        entityManager.flush();
        entityManager.clear();

        // Try to retrieve the deleted event
        Event deletedEvent = entityManager.find(Event.class, event.getId());

        // Assert the event was deleted
        assertNull(deletedEvent);
    }

    @Test
    void testEventQuery() {
        // Create and persist multiple events
        Event event1 = new Event();
        event1.setTitle("Event 1");
        event1.setDescription("Description 1");
        event1.setDateTime(LocalDateTime.now().plusDays(1));
        event1.setLocation("Location 1");
        event1.setCategory("CONFERENCE");
        event1.setPrice(99.99);
        event1.setOrganizerId(1L);
        event1.setStatus("ACTIVE");

        Event event2 = new Event();
        event2.setTitle("Event 2");
        event2.setDescription("Description 2");
        event2.setDateTime(LocalDateTime.now().plusDays(2));
        event2.setLocation("Location 2");
        event2.setCategory("WORKSHOP");
        event2.setPrice(149.99);
        event2.setOrganizerId(1L);
        event2.setStatus("ACTIVE");

        entityManager.persist(event1);
        entityManager.persist(event2);
        entityManager.flush();
        entityManager.clear();

        // Query events by category
        List<Event> conferenceEvents = entityManager.createQuery(
                "SELECT e FROM Event e WHERE e.category = :category", Event.class)
                .setParameter("category", "CONFERENCE")
                .getResultList();

        // Assert the query results
        assertEquals(1, conferenceEvents.size());
        assertEquals("Event 1", conferenceEvents.get(0).getTitle());
    }
} 