package com.surest.memberapp.config;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    // Provide a test secret key same length as production key (e.g. 256 bit base64 or UTF-8)
    private final String testSecret = "test-secure-secret-key-of-adequate-length-1234567890";

    // Set expiration for 1 hour in milliseconds
    private final long expiration = 3600000;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        jwtService.secretKey = testSecret;
        jwtService.expirationTime = expiration;
    }

    @Test
    void generateToken_shouldContainUsernameAndRole() {
        User userDetails = new User("testuser", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotBlank();

        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo("testuser");

        Claims claims = jwtService.parseClaims(token);
        assertThat(claims.get("role")).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        User userDetails = new User("user1", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        String token = jwtService.generateToken(userDetails);

        boolean valid = jwtService.isTokenValid(token, userDetails);

        assertThat(valid).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalseForExpiredToken() throws InterruptedException {
        // Override expiration to 1ms to force expiration quickly
        jwtService.expirationTime = 1;

        User userDetails = new User("user2", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        String token = jwtService.generateToken(userDetails);

        // Wait for token to expire
        Thread.sleep(10);

        boolean valid = jwtService.isTokenValid(token, userDetails);

        assertThat(valid).isFalse();
    }

    @Test
    void isTokenValid_shouldReturnFalseForDifferentUsername() {
        User userDetails1 = new User("userA", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        User userDetails2 = new User("userB", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        String token = jwtService.generateToken(userDetails1);

        boolean valid = jwtService.isTokenValid(token, userDetails2);

        assertThat(valid).isFalse();
    }
}
