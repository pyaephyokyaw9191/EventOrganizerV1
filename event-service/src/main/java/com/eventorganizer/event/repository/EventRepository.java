package com.eventorganizer.event.repository;

import com.eventorganizer.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCategory(String category);
    List<Event> findByLocationAndDateTimeBetween(String location, LocalDateTime startDate, LocalDateTime endDate);
    List<Event> findByOrganizerId(Long organizerId);
    List<Event> findByStatus(String status);
    List<Event> findByPriceLessThanEqual(double price);
    List<Event> findByDateTimeGreaterThanEqual(LocalDateTime dateTime);
} 