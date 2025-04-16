package com.eventorganizer.auth.service;

import com.eventorganizer.auth.model.Role;
import com.eventorganizer.auth.model.User;
import com.eventorganizer.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User user;
    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username(TEST_USERNAME)
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.ATTENDEE)
                .build();
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_USERNAME);

        assertNotNull(userDetails);
        assertEquals(TEST_USERNAME, userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Role.ATTENDEE.name())));
        verify(userRepository).findByUsername(TEST_USERNAME);
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsername(TEST_USERNAME)
        );
        verify(userRepository).findByUsername(TEST_USERNAME);
    }
} 