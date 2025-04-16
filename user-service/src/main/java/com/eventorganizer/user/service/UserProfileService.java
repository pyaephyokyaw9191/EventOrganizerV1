package com.eventorganizer.user.service;

import com.eventorganizer.user.model.UserProfile;
import com.eventorganizer.user.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional(readOnly = true)
    public UserProfile getUserProfile(String username) {
        return userProfileRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User profile not found for username: " + username));
    }

    @Transactional
    public UserProfile updateUserProfile(String username, UserProfile updatedProfile) {
        UserProfile existingProfile = getUserProfile(username);
        
        // Update only non-null fields
        if (updatedProfile.getFirstName() != null) {
            existingProfile.setFirstName(updatedProfile.getFirstName());
        }
        if (updatedProfile.getLastName() != null) {
            existingProfile.setLastName(updatedProfile.getLastName());
        }
        if (updatedProfile.getEmail() != null && !updatedProfile.getEmail().equals(existingProfile.getEmail())) {
            if (userProfileRepository.existsByEmail(updatedProfile.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            existingProfile.setEmail(updatedProfile.getEmail());
        }
        if (updatedProfile.getPhone() != null) {
            existingProfile.setPhone(updatedProfile.getPhone());
        }
        if (updatedProfile.getBio() != null) {
            existingProfile.setBio(updatedProfile.getBio());
        }
        if (updatedProfile.getProfilePictureUrl() != null) {
            existingProfile.setProfilePictureUrl(updatedProfile.getProfilePictureUrl());
        }

        return userProfileRepository.save(existingProfile);
    }

    @Transactional
    public UserProfile createUserProfile(UserProfile userProfile) {
        if (userProfileRepository.existsByUsername(userProfile.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userProfileRepository.existsByEmail(userProfile.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        return userProfileRepository.save(userProfile);
    }

    @Transactional
    public void deleteUserProfile(String username) {
        UserProfile userProfile = getUserProfile(username);
        userProfileRepository.delete(userProfile);
    }
} 