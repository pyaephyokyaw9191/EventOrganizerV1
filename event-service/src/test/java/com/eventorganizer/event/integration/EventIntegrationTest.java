package com.eventorganizer.event.integration;

import com.eventorganizer.event.model.Event;
import com.eventorganizer.event.repository.EventRepository;
import com.eventorganizer.event.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EventIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    private Event testEvent;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        eventRepository.deleteAll();
        
        now = LocalDateTime.now();
        
        // Create a test event
        testEvent = new Event();
        testEvent.setTitle("Integration Test Event");
        testEvent.setDescription("Test Description");
        testEvent.setDateTime(now.plusDays(1));
        testEvent.setLocation("Test Location");
        testEvent.setCategory("CONFERENCE");
        testEvent.setPrice(99.99);
        testEvent.setOrganizerId(1L);
        testEvent.setStatus("ACTIVE");
        
        // Save the test event to the database
        testEvent = eventRepository.save(testEvent);
    }

    @Test
    void createEvent_ValidEvent_ReturnsCreated() throws Exception {
        // Create a new event
        Event newEvent = new Event();
        newEvent.setTitle("New Integration Test Event");
        newEvent.setDescription("New Test Description");
        newEvent.setDateTime(now.plusDays(2));
        newEvent.setLocation("New Test Location");
        newEvent.setCategory("WORKSHOP");
        newEvent.setPrice(149.99);
        newEvent.setOrganizerId(2L);
        newEvent.setStatus("ACTIVE");

        // Perform the POST request
        String response = mockMvc.perform(post("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEvent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(newEvent.getTitle()))
                .andExpect(jsonPath("$.description").value(newEvent.getDescription()))
                .andExpect(jsonPath("$.location").value(newEvent.getLocation()))
                .andExpect(jsonPath("$.category").value(newEvent.getCategory()))
                .andExpect(jsonPath("$.price").value(newEvent.getPrice()))
                .andExpect(jsonPath("$.organizerId").value(newEvent.getOrganizerId()))
                .andExpect(jsonPath("$.status").value(newEvent.getStatus()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Parse the response
        Event createdEvent = objectMapper.readValue(response, Event.class);

        // Verify the event was saved to the database
        Event savedEvent = eventRepository.findById(createdEvent.getId()).orElse(null);
        assertNotNull(savedEvent);
        assertEquals(newEvent.getTitle(), savedEvent.getTitle());
        assertEquals(newEvent.getDescription(), savedEvent.getDescription());
        assertEquals(newEvent.getLocation(), savedEvent.getLocation());
        assertEquals(newEvent.getCategory(), savedEvent.getCategory());
        assertEquals(newEvent.getPrice(), savedEvent.getPrice());
        assertEquals(newEvent.getOrganizerId(), savedEvent.getOrganizerId());
        assertEquals(newEvent.getStatus(), savedEvent.getStatus());
    }

    @Test
    void getEvent_ExistingEvent_ReturnsEvent() throws Exception {
        // Perform the GET request
        mockMvc.perform(get("/api/v1/events/{id}", testEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testEvent.getId()))
                .andExpect(jsonPath("$.title").value(testEvent.getTitle()))
                .andExpect(jsonPath("$.description").value(testEvent.getDescription()))
                .andExpect(jsonPath("$.location").value(testEvent.getLocation()))
                .andExpect(jsonPath("$.category").value(testEvent.getCategory()))
                .andExpect(jsonPath("$.price").value(testEvent.getPrice()))
                .andExpect(jsonPath("$.organizerId").value(testEvent.getOrganizerId()))
                .andExpect(jsonPath("$.status").value(testEvent.getStatus()));
    }

    @Test
    void updateEvent_Authorized_UpdatesEvent() throws Exception {
        // Create an updated event
        Event updatedEvent = new Event();
        updatedEvent.setTitle("Updated Integration Test Event");
        updatedEvent.setDescription("Updated Test Description");
        updatedEvent.setDateTime(now.plusDays(3));
        updatedEvent.setLocation("Updated Test Location");
        updatedEvent.setCategory("SEMINAR");
        updatedEvent.setPrice(199.99);
        updatedEvent.setOrganizerId(testEvent.getOrganizerId());
        updatedEvent.setStatus("ACTIVE");

        // Perform the PUT request
        mockMvc.perform(put("/api/v1/events/{id}", testEvent.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Organizer-Id", testEvent.getOrganizerId())
                .content(objectMapper.writeValueAsString(updatedEvent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testEvent.getId()))
                .andExpect(jsonPath("$.title").value(updatedEvent.getTitle()))
                .andExpect(jsonPath("$.description").value(updatedEvent.getDescription()))
                .andExpect(jsonPath("$.location").value(updatedEvent.getLocation()))
                .andExpect(jsonPath("$.category").value(updatedEvent.getCategory()))
                .andExpect(jsonPath("$.price").value(updatedEvent.getPrice()));

        // Verify the event was updated in the database
        Event savedEvent = eventRepository.findById(testEvent.getId()).orElse(null);
        assertNotNull(savedEvent);
        assertEquals(updatedEvent.getTitle(), savedEvent.getTitle());
        assertEquals(updatedEvent.getDescription(), savedEvent.getDescription());
        assertEquals(updatedEvent.getLocation(), savedEvent.getLocation());
        assertEquals(updatedEvent.getCategory(), savedEvent.getCategory());
        assertEquals(updatedEvent.getPrice(), savedEvent.getPrice());
    }

    @Test
    void cancelEvent_Authorized_CancelsEvent() throws Exception {
        // Perform the DELETE request
        mockMvc.perform(delete("/api/v1/events/{id}", testEvent.getId())
                .header("X-Organizer-Id", testEvent.getOrganizerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testEvent.getId()))
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        // Verify the event was cancelled in the database
        Event savedEvent = eventRepository.findById(testEvent.getId()).orElse(null);
        assertNotNull(savedEvent);
        assertEquals("CANCELLED", savedEvent.getStatus());
    }

    @Test
    void findEventsByCategory_ReturnsFilteredEvents() throws Exception {
        // Create additional events with different categories
        Event workshopEvent = new Event();
        workshopEvent.setTitle("Workshop Event");
        workshopEvent.setDescription("Workshop Description");
        workshopEvent.setDateTime(now.plusDays(2));
        workshopEvent.setLocation("Workshop Location");
        workshopEvent.setCategory("WORKSHOP");
        workshopEvent.setPrice(149.99);
        workshopEvent.setOrganizerId(1L);
        workshopEvent.setStatus("ACTIVE");
        eventRepository.save(workshopEvent);

        // Perform the GET request with category filter
        mockMvc.perform(get("/api/v1/events")
                .param("category", "CONFERENCE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testEvent.getId()))
                .andExpect(jsonPath("$[0].category").value("CONFERENCE"));

        // Verify the correct events were returned
        List<Event> conferenceEvents = eventRepository.findByCategory("CONFERENCE");
        assertEquals(1, conferenceEvents.size());
        assertEquals("CONFERENCE", conferenceEvents.get(0).getCategory());
    }

    @Test
    void findUpcomingEvents_ReturnsFutureEvents() throws Exception {
        // Create another future event with a later date
        Event laterEvent = new Event();
        laterEvent.setTitle("Later Event");
        laterEvent.setDescription("Later Description");
        laterEvent.setDateTime(now.plusDays(2));
        laterEvent.setLocation("Later Location");
        laterEvent.setCategory("CONFERENCE");
        laterEvent.setPrice(99.99);
        laterEvent.setOrganizerId(1L);
        laterEvent.setStatus("ACTIVE");
        eventRepository.save(laterEvent);

        // Perform the GET request for upcoming events
        mockMvc.perform(get("/api/v1/events/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").exists())
                .andExpect(jsonPath("$[*].title").exists())
                .andExpect(jsonPath("$[*].dateTime").exists());

        // Verify all events are in the future
        List<Event> upcomingEvents = eventRepository.findByDateTimeGreaterThanEqual(now);
        assertTrue(upcomingEvents.size() >= 2);
        for (Event event : upcomingEvents) {
            assertTrue(event.getDateTime().isAfter(now) || event.getDateTime().isEqual(now));
        }
    }
} 