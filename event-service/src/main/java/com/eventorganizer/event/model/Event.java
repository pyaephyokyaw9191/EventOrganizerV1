package com.eventorganizer.event.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @NotNull(message = "Date and time is required")
    @FutureOrPresent(message = "Event date must be in the present or future")
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @NotBlank(message = "Location is required")
    @Column(nullable = false)
    private String location;

    @NotBlank(message = "Category is required")
    @Column(nullable = false)
    private String category;

    @PositiveOrZero(message = "Price must be zero or positive")
    @Column(nullable = false)
    private double price;

    @NotNull(message = "Organizer ID is required")
    @Positive(message = "Organizer ID must be positive")
    @Column(name = "organizer_id", nullable = false)
    private Long organizerId;

    @NotBlank(message = "Status is required")
    @Column(nullable = false)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 