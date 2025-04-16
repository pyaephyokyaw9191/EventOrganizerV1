package com.eventorganizer.auth.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private UserDetails userDetails;
    private static final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final Long EXPIRATION = 86400000L; // 1 day

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtils, "expiration", EXPIRATION);

        userDetails = new User(
                "testuser",
                "password",
                Collections.singletonList(() -> "ROLE_ATTENDEE")
        );
    }

    @Test
    void generateToken_ValidUserDetails_ReturnsToken() {
        String token = jwtUtils.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void extractUsername_ValidToken_ReturnsUsername() {
        String token = jwtUtils.generateToken(userDetails);
        String username = jwtUtils.extractUsername(token);

        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String token = jwtUtils.generateToken(userDetails);
        boolean isValid = jwtUtils.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        // Create a token with a different username
        UserDetails differentUser = new User(
                "different_user",
                "password",
                Collections.singletonList(() -> "ROLE_ATTENDEE")
        );
        
        String token = jwtUtils.generateToken(differentUser);
        boolean isValid = jwtUtils.validateToken(token, userDetails);

        assertFalse(isValid);
    }

    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        // Create a token with a past expiration date
        JwtUtils expiredJwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(expiredJwtUtils, "secret", SECRET);
        ReflectionTestUtils.setField(expiredJwtUtils, "expiration", -10000L);

        String token = expiredJwtUtils.generateToken(userDetails);

        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtils.validateToken(token, userDetails);
        });
    }
} 