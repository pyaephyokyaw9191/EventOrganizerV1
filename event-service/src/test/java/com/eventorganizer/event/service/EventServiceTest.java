package com.eventorganizer.event.service;

import com.eventorganizer.event.model.Event;
import com.eventorganizer.event.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event event;
    private static final Long EVENT_ID = 1L;
    private static final Long ORGANIZER_ID = 1L;

    @BeforeEach
    void setUp() {
        event = Event.builder()
                .id(EVENT_ID)
                .title("Tech Conference 2024")
                .description("Annual technology conference")
                .dateTime(LocalDateTime.now().plusDays(1))
                .location("Convention Center")
                .category("CONFERENCE")
                .price(99.99)
                .organizerId(ORGANIZER_ID)
                .status("ACTIVE")
                .build();
    }

    @Test
    void createEvent_WithValidData_Success() {
        // Arrange
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        // Act
        Event created = eventService.createEvent(event);

        // Assert
        assertNotNull(created);
        assertEquals(EVENT_ID, created.getId());
        assertEquals("Tech Conference 2024", created.getTitle());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void createEvent_WithPastDate_ThrowsException() {
        // Arrange
        Event pastEvent = Event.builder()
                .title("Past Event")
                .dateTime(LocalDateTime.now().minusDays(1))
                .location("Venue")
                .category("CONFERENCE")
                .organizerId(ORGANIZER_ID)
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> eventService.createEvent(pastEvent));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void getEventById_WhenEventExists_ReturnsEvent() {
        // Arrange
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

        // Act
        Event found = eventService.getEventById(EVENT_ID);

        // Assert
        assertNotNull(found);
        assertEquals(EVENT_ID, found.getId());
        assertEquals("Tech Conference 2024", found.getTitle());
        verify(eventRepository).findById(EVENT_ID);
    }

    @Test
    void getEventById_WhenEventDoesNotExist_ThrowsException() {
        // Arrange
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> eventService.getEventById(EVENT_ID));
        verify(eventRepository).findById(EVENT_ID);
    }

    @Test
    void updateEvent_WhenEventExistsAndOrganizerValid_UpdatesEvent() {
        // Arrange
        Event updatedEvent = Event.builder()
                .id(EVENT_ID)
                .title("Updated Conference 2024")
                .description("Updated description")
                .dateTime(LocalDateTime.now().plusDays(2))
                .location("New Venue")
                .category("CONFERENCE")
                .price(149.99)
                .organizerId(ORGANIZER_ID)
                .status("ACTIVE")
                .build();

        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);

        // Act
        Event result = eventService.updateEvent(EVENT_ID, updatedEvent, ORGANIZER_ID);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Conference 2024", result.getTitle());
        assertEquals("Updated description", result.getDescription());
        assertEquals(149.99, result.getPrice());
        verify(eventRepository).findById(EVENT_ID);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void updateEvent_WhenNotOrganizer_ThrowsException() {
        // Arrange
        Long differentOrganizerId = 2L;
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> eventService.updateEvent(EVENT_ID, event, differentOrganizerId));
        verify(eventRepository).findById(EVENT_ID);
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void cancelEvent_WhenEventExistsAndOrganizerValid_CancelsEvent() {
        // Arrange
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Event cancelled = eventService.cancelEvent(EVENT_ID, ORGANIZER_ID);

        // Assert
        assertNotNull(cancelled);
        assertEquals("CANCELLED", cancelled.getStatus());
        verify(eventRepository).findById(EVENT_ID);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void cancelEvent_WhenNotOrganizer_ThrowsException() {
        // Arrange
        Long differentOrganizerId = 2L;
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> eventService.cancelEvent(EVENT_ID, differentOrganizerId));
        verify(eventRepository).findById(EVENT_ID);
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void findEventsByCategory_WhenEventsExist_ReturnsEvents() {
        // Arrange
        List<Event> events = Arrays.asList(event);
        when(eventRepository.findByCategory("CONFERENCE")).thenReturn(events);

        // Act
        List<Event> found = eventService.findEventsByCategory("CONFERENCE");

        // Assert
        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
        assertEquals("Tech Conference 2024", found.get(0).getTitle());
        verify(eventRepository).findByCategory("CONFERENCE");
    }

    @Test
    void findEventsByLocation_WhenEventsExist_ReturnsEvents() {
        // Arrange
        List<Event> events = Arrays.asList(event);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(7);
        
        when(eventRepository.findByLocationAndDateTimeBetween("Convention Center", start, end))
                .thenReturn(events);

        // Act
        List<Event> found = eventService.findEventsByLocation("Convention Center", start, end);

        // Assert
        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
        assertEquals("Tech Conference 2024", found.get(0).getTitle());
        verify(eventRepository).findByLocationAndDateTimeBetween("Convention Center", start, end);
    }

    @Test
    void findEventsByOrganizer_WhenEventsExist_ReturnsEvents() {
        // Arrange
        List<Event> events = Arrays.asList(event);
        when(eventRepository.findByOrganizerId(ORGANIZER_ID)).thenReturn(events);

        // Act
        List<Event> found = eventService.findEventsByOrganizer(ORGANIZER_ID);

        // Assert
        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
        assertEquals("Tech Conference 2024", found.get(0).getTitle());
        verify(eventRepository).findByOrganizerId(ORGANIZER_ID);
    }

    @Test
    void findUpcomingEvents_WhenEventsExist_ReturnsEvents() {
        // Arrange
        List<Event> events = Arrays.asList(event);
        when(eventRepository.findByDateTimeGreaterThanEqual(any(LocalDateTime.class)))
                .thenReturn(events);

        // Act
        List<Event> found = eventService.findUpcomingEvents();

        // Assert
        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
        assertEquals("Tech Conference 2024", found.get(0).getTitle());
        verify(eventRepository).findByDateTimeGreaterThanEqual(any(LocalDateTime.class));
    }
} 