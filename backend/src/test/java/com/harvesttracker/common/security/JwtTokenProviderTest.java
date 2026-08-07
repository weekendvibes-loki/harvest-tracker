package com.harvesttracker.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private static final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(SECRET, 900000L, 604800000L);
    }

    @Test
    @DisplayName("Should generate valid Access Token with claims")
    void generateAccessToken_Success() {
        Long userId = 1L;
        String email = "admin@harvesttracker.local";
        String role = "ADMIN";
        List<String> permissions = List.of("AUTH_USER_MANAGE", "FARM_VIEW");

        String token = tokenProvider.generateAccessToken(userId, email, role, permissions);

        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertTrue(tokenProvider.isAccessToken(token));
        assertFalse(tokenProvider.isRefreshToken(token));

        assertEquals(email, tokenProvider.getUsernameFromToken(token));
        assertEquals(userId, tokenProvider.getUserIdFromToken(token));
        assertEquals(role, tokenProvider.getRoleFromToken(token));
        assertEquals(permissions, tokenProvider.getPermissionsFromToken(token));
    }

    @Test
    @DisplayName("Should generate valid Refresh Token")
    void generateRefreshToken_Success() {
        Long userId = 1L;
        String email = "admin@harvesttracker.local";

        String token = tokenProvider.generateRefreshToken(userId, email);

        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertTrue(tokenProvider.isRefreshToken(token));
        assertFalse(tokenProvider.isAccessToken(token));

        assertEquals(email, tokenProvider.getUsernameFromToken(token));
        assertEquals(userId, tokenProvider.getUserIdFromToken(token));
    }

    @Test
    @DisplayName("Should reject invalid or tampered token")
    void validateToken_Invalid() {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.invalid.token";

        assertFalse(tokenProvider.validateToken(invalidToken));
        assertFalse(tokenProvider.isAccessToken(invalidToken));
        assertFalse(tokenProvider.isRefreshToken(invalidToken));
    }

    @Test
    @DisplayName("Should reject expired token")
    void validateToken_Expired() {
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(SECRET, 1L, 1L);
        String token = shortLivedProvider.generateAccessToken(1L, "user@test.com", "ADMIN", List.of());

        try {
            Thread.sleep(10);
        } catch (InterruptedException ignored) {}

        assertFalse(shortLivedProvider.validateToken(token));
    }
}
