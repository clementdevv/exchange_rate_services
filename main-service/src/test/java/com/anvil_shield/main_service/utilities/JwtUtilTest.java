package com.anvil_shield.main_service.utilities;

import com.anvil_shield.main_service.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private final String secret = "test-secret-key";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Manually inject the secret key using reflection
        try {
            var field = JwtUtil.class.getDeclaredField("SECRET_KEY");
            field.setAccessible(true);
            field.set(jwtUtil, secret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGenerateAndExtractToken() {
        // Mock user details
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token);

        String extractedEmail = jwtUtil.extractEmail(token);
        assertEquals("test@example.com", extractedEmail);

        Date expiration = jwtUtil.extractExpiration(token);
        assertTrue(expiration.after(new Date()));

        assertFalse(jwtUtil.isTokenExpired(token));
        assertTrue(jwtUtil.isValidToken(token, userDetails));
    }


}
