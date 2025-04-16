package com.eventorganizer.registration.repository;

import com.eventorganizer.registration.model.Registration;
import com.eventorganizer.registration.model.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByUserId(Long userId);
    List<Registration> findByEventId(Long eventId);
    List<Registration> findByStatus(RegistrationStatus status);
} 