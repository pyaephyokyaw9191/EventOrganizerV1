package com.eventorganizer.registration.controller;

import com.eventorganizer.registration.model.Registration;
import com.eventorganizer.registration.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Registration Service");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Registration> registerForEvent(@Valid @RequestBody RegistrationRequest request) {
        System.out.println("Received registration request: " + request);
        try {
            Registration registration = registrationService.registerForEvent(request.getEventId(), request.getUserId());
            System.out.println("Registration created: " + registration);
            return new ResponseEntity<>(registration, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            System.out.println("Registration failed: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error during registration: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }

    @GetMapping
    public ResponseEntity<List<Registration>> getRegistrationsByUserId(@RequestParam Long userId) {
        List<Registration> registrations = registrationService.getRegistrationsByUserId(userId);
        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Registration> getRegistrationById(@PathVariable Long id) {
        try {
            Registration registration = registrationService.getRegistrationById(id);
            return ResponseEntity.ok(registration);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Registration> cancelRegistration(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            Registration registration = registrationService.cancelRegistration(id, userId);
            return ResponseEntity.ok(registration);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Not authorized")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
            } else if (e.getMessage().contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }
    }
} 