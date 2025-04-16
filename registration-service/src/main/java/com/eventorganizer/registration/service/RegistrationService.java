package com.eventorganizer.registration.service;

import com.eventorganizer.registration.model.Registration;
import com.eventorganizer.registration.model.RegistrationStatus;
import com.eventorganizer.registration.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final RestTemplate restTemplate;
    private final String eventServiceUrl;

    @Autowired
    public RegistrationService(
            RegistrationRepository registrationRepository,
            RestTemplate restTemplate,
            @Value("${event.service.url}") String eventServiceUrl) {
        this.registrationRepository = registrationRepository;
        this.restTemplate = restTemplate;
        this.eventServiceUrl = eventServiceUrl;
    }

    public Registration registerForEvent(Long eventId, Long userId) {
        System.out.println("Registering for event: " + eventId + ", user: " + userId);
        
        // Check if event exists
        String url = eventServiceUrl + "/" + eventId + "/exists";
        System.out.println("Checking if event exists at URL: " + url);
        
        try {
            Boolean eventExists = restTemplate.getForObject(url, Boolean.class);
            System.out.println("Event exists check result: " + eventExists);

            if (eventExists == null || !eventExists) {
                System.out.println("Event not found");
                throw new IllegalArgumentException("Event not found");
            }
        } catch (Exception e) {
            System.out.println("Error checking if event exists: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException("Error checking if event exists: " + e.getMessage());
        }

        // Create new registration
        Registration registration = new Registration();
        registration.setEventId(eventId);
        registration.setUserId(userId);
        registration.setRegistrationDate(LocalDateTime.now());
        registration.setStatus(RegistrationStatus.REGISTERED);
        registration.setTicketToken(generateTicketToken());

        System.out.println("Saving registration: " + registration);
        return registrationRepository.save(registration);
    }

    public List<Registration> getRegistrationsByUserId(Long userId) {
        return registrationRepository.findByUserId(userId);
    }

    public Registration cancelRegistration(Long registrationId, Long userId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found"));

        if (!registration.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized");
        }

        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new IllegalArgumentException("Registration is already cancelled");
        }

        registration.setStatus(RegistrationStatus.CANCELLED);
        return registrationRepository.save(registration);
    }

    public Registration getRegistrationById(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found"));
    }

    private String generateTicketToken() {
        return UUID.randomUUID().toString();
    }
} 