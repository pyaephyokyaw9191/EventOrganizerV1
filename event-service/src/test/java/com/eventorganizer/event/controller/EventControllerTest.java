package com.eventorganizer.event.controller;

import com.eventorganizer.event.config.TestConfig;
import com.eventorganizer.event.model.Event;
import com.eventorganizer.event.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@Import(TestConfig.class)
@ActiveProfiles("test")
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    private Event testEvent;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("Test Event");
        testEvent.setDescription("Test Description");
        testEvent.setDateTime(now.plusDays(1));
        testEvent.setLocation("Test Location");
        testEvent.setCategory("Test Category");
        testEvent.setPrice(10.0);
        testEvent.setOrganizerId(1L);
        testEvent.setStatus("ACTIVE");
    }

    @Test
    void createEvent_ValidEvent_ReturnsCreated() throws Exception {
        when(eventService.createEvent(any(Event.class))).thenReturn(testEvent);

        mockMvc.perform(post("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEvent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testEvent.getId()));
    }

    @Test
    void createEvent_InvalidEvent_ReturnsBadRequest() throws Exception {
        testEvent.setTitle(null); // Invalid event - title is required

        mockMvc.perform(post("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEvent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEvent_Unauthorized_ReturnsForbidden() throws Exception {
        Long organizerId = 2L; // Different from event's organizerId
        when(eventService.updateEvent(eq(1L), any(Event.class), eq(organizerId)))
                .thenThrow(new RuntimeException("Not authorized to update this event"));

        mockMvc.perform(put("/api/v1/events/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Organizer-Id", organizerId)
                .content(objectMapper.writeValueAsString(testEvent)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateEvent_Authorized_ReturnsOk() throws Exception {
        Long organizerId = 1L; // Same as event's organizerId
        when(eventService.updateEvent(eq(1L), any(Event.class), eq(organizerId)))
                .thenReturn(testEvent);

        mockMvc.perform(put("/api/v1/events/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Organizer-Id", organizerId)
                .content(objectMapper.writeValueAsString(testEvent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testEvent.getId()));
    }

    @Test
    void cancelEvent_Unauthorized_ReturnsForbidden() throws Exception {
        Long organizerId = 2L; // Different from event's organizerId
        when(eventService.cancelEvent(eq(1L), eq(organizerId)))
                .thenThrow(new RuntimeException("Not authorized to cancel this event"));

        mockMvc.perform(delete("/api/v1/events/1")
                .header("X-Organizer-Id", organizerId))
                .andExpect(status().isForbidden());
    }

    @Test
    void cancelEvent_Authorized_ReturnsOk() throws Exception {
        Long organizerId = 1L; // Same as event's organizerId
        when(eventService.cancelEvent(eq(1L), eq(organizerId)))
                .thenReturn(testEvent);

        mockMvc.perform(delete("/api/v1/events/1")
                .header("X-Organizer-Id", organizerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testEvent.getId()));
    }

    @Test
    void getEvent_ExistingEvent_ReturnsOk() throws Exception {
        when(eventService.getEventById(1L)).thenReturn(testEvent);

        mockMvc.perform(get("/api/v1/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testEvent.getId()));
    }

    @Test
    void getEvent_NonExistingEvent_ReturnsNotFound() throws Exception {
        when(eventService.getEventById(1L))
                .thenThrow(new RuntimeException("Event not found"));

        mockMvc.perform(get("/api/v1/events/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findEvents_WithCategory_ReturnsFilteredEvents() throws Exception {
        when(eventService.findEventsByCategory("Test Category"))
                .thenReturn(Arrays.asList(testEvent));

        mockMvc.perform(get("/api/v1/events")
                .param("category", "Test Category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testEvent.getId()))
                .andExpect(jsonPath("$[0].category").value(testEvent.getCategory()));
    }

    @Test
    void findEvents_WithLocationAndDates_ReturnsFilteredEvents() throws Exception {
        LocalDateTime startDate = now;
        LocalDateTime endDate = now.plusDays(7);
        
        when(eventService.findEventsByLocation("Test Location", startDate, endDate))
                .thenReturn(Arrays.asList(testEvent));

        mockMvc.perform(get("/api/v1/events")
                .param("location", "Test Location")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testEvent.getId()))
                .andExpect(jsonPath("$[0].location").value(testEvent.getLocation()));

        verify(eventService).findEventsByLocation("Test Location", startDate, endDate);
    }

    @Test
    void findEvents_WithNoParameters_ReturnsUpcomingEvents() throws Exception {
        when(eventService.findUpcomingEvents())
                .thenReturn(Arrays.asList(testEvent));

        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testEvent.getId()));

        verify(eventService).findUpcomingEvents();
    }

    @Test
    void findEvents_WithInvalidDateRange_ReturnsBadRequest() throws Exception {
        LocalDateTime startDate = now.plusDays(7);
        LocalDateTime endDate = now; // End date before start date

        mockMvc.perform(get("/api/v1/events")
                .param("location", "Test Location")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).findEventsByLocation(any(), any(), any());
    }

    @Test
    void findUpcomingEvents_ReturnsEvents() throws Exception {
        Event upcomingEvent1 = new Event();
        upcomingEvent1.setId(1L);
        upcomingEvent1.setTitle("Upcoming Event 1");
        upcomingEvent1.setDateTime(now.plusDays(1));

        Event upcomingEvent2 = new Event();
        upcomingEvent2.setId(2L);
        upcomingEvent2.setTitle("Upcoming Event 2");
        upcomingEvent2.setDateTime(now.plusDays(2));

        List<Event> upcomingEvents = Arrays.asList(upcomingEvent1, upcomingEvent2);
        when(eventService.findUpcomingEvents()).thenReturn(upcomingEvents);

        mockMvc.perform(get("/api/v1/events/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(upcomingEvent1.getId()))
                .andExpect(jsonPath("$[0].title").value(upcomingEvent1.getTitle()))
                .andExpect(jsonPath("$[1].id").value(upcomingEvent2.getId()))
                .andExpect(jsonPath("$[1].title").value(upcomingEvent2.getTitle()));

        verify(eventService).findUpcomingEvents();
    }

    @Test
    void findUpcomingEvents_WhenNoEvents_ReturnsEmptyList() throws Exception {
        when(eventService.findUpcomingEvents()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/events/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(eventService).findUpcomingEvents();
    }
} 