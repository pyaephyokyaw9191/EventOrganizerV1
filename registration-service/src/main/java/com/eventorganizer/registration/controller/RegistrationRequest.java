package com.eventorganizer.registration.controller;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotNull(message = "User ID is required")
    private Long userId;
    
    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "eventId=" + eventId +
                ", userId=" + userId +
                '}';
    }
} 