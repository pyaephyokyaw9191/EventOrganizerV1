package com.eventorganizer.auth.service;

import com.eventorganizer.auth.dto.AuthResponse;
import com.eventorganizer.auth.dto.LoginRequest;
import com.eventorganizer.auth.dto.SignupRequest;
import com.eventorganizer.auth.model.Role;
import com.eventorganizer.auth.model.User;
import com.eventorganizer.auth.repository.UserRepository;
import com.eventorganizer.auth.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private User user;
    private String encodedPassword;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password");
        signupRequest.setRole(Role.ATTENDEE);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        encodedPassword = "encodedPassword";
        jwtToken = "jwtToken";

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password(encodedPassword)
                .role(Role.ATTENDEE)
                .build();
    }

    @Test
    void signup_Success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtils.generateToken(any())).thenReturn(jwtToken);

        AuthResponse response = authenticationService.signup(signupRequest);

        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getRole().name(), response.getRole());

        verify(userRepository).existsByUsername(signupRequest.getUsername());
        verify(userRepository).existsByEmail(signupRequest.getEmail());
        verify(passwordEncoder).encode(signupRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtUtils).generateToken(any());
    }

    @Test
    void signup_UsernameExists_ThrowsException() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authenticationService.signup(signupRequest));
        verify(userRepository).existsByUsername(signupRequest.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(any())).thenReturn(jwtToken);

        AuthResponse response = authenticationService.login(loginRequest);

        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getRole().name(), response.getRole());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername(loginRequest.getUsername());
        verify(jwtUtils).generateToken(any());
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authenticationService.login(loginRequest));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername(loginRequest.getUsername());
        verify(jwtUtils, never()).generateToken(any());
    }
} 