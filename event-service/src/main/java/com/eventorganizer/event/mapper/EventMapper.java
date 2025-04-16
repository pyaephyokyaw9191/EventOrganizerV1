package com.eventorganizer.event.mapper;

import com.eventorganizer.event.dto.EventDTO;
import com.eventorganizer.event.model.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventDTO toDTO(Event event) {
        if (event == null) {
            return null;
        }

        return EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .dateTime(event.getDateTime())
                .location(event.getLocation())
                .category(event.getCategory())
                .price(event.getPrice())
                .organizerId(event.getOrganizerId())
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

    public Event toEntity(EventDTO eventDTO) {
        if (eventDTO == null) {
            return null;
        }

        return Event.builder()
                .id(eventDTO.getId())
                .title(eventDTO.getTitle())
                .description(eventDTO.getDescription())
                .dateTime(eventDTO.getDateTime())
                .location(eventDTO.getLocation())
                .category(eventDTO.getCategory())
                .price(eventDTO.getPrice())
                .organizerId(eventDTO.getOrganizerId())
                .status(eventDTO.getStatus())
                .build();
    }

    public void updateEventFromDTO(Event event, EventDTO eventDTO) {
        if (eventDTO == null) {
            return;
        }

        if (eventDTO.getTitle() != null) {
            event.setTitle(eventDTO.getTitle());
        }
        if (eventDTO.getDescription() != null) {
            event.setDescription(eventDTO.getDescription());
        }
        if (eventDTO.getDateTime() != null) {
            event.setDateTime(eventDTO.getDateTime());
        }
        if (eventDTO.getLocation() != null) {
            event.setLocation(eventDTO.getLocation());
        }
        if (eventDTO.getCategory() != null) {
            event.setCategory(eventDTO.getCategory());
        }
        if (eventDTO.getPrice() != null) {
            event.setPrice(eventDTO.getPrice());
        }
        if (eventDTO.getStatus() != null) {
            event.setStatus(eventDTO.getStatus());
        }
    }
} 