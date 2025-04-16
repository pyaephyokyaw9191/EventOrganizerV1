package com.eventorganizer.event.service;

import com.eventorganizer.event.model.Event;
import com.eventorganizer.event.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event createEvent(Event event) {
        if (event.getDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Event date must be in the future");
        }
        return eventRepository.save(event);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
    }

    public Event updateEvent(Long id, Event updatedEvent, Long organizerId) {
        Event event = getEventById(id);
        if (!event.getOrganizerId().equals(organizerId)) {
            throw new RuntimeException("Not authorized to update this event");
        }
        
        event.setTitle(updatedEvent.getTitle());
        event.setDescription(updatedEvent.getDescription());
        event.setDateTime(updatedEvent.getDateTime());
        event.setLocation(updatedEvent.getLocation());
        event.setCategory(updatedEvent.getCategory());
        event.setPrice(updatedEvent.getPrice());
        event.setStatus(updatedEvent.getStatus());
        
        return eventRepository.save(event);
    }

    public Event cancelEvent(Long id, Long organizerId) {
        Event event = getEventById(id);
        if (!event.getOrganizerId().equals(organizerId)) {
            throw new RuntimeException("Not authorized to cancel this event");
        }
        event.setStatus("CANCELLED");
        return eventRepository.save(event);
    }

    public List<Event> findEventsByCategory(String category) {
        return eventRepository.findByCategory(category);
    }

    public List<Event> findEventsByLocation(String location, LocalDateTime startDate, LocalDateTime endDate) {
        return eventRepository.findByLocationAndDateTimeBetween(location, startDate, endDate);
    }

    public List<Event> findEventsByOrganizer(Long organizerId) {
        return eventRepository.findByOrganizerId(organizerId);
    }

    public List<Event> findUpcomingEvents() {
        return eventRepository.findByDateTimeGreaterThanEqual(LocalDateTime.now());
    }
} 