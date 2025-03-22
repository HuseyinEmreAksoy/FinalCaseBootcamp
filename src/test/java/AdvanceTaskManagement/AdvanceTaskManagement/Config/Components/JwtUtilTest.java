package AdvanceTaskManagement.AdvanceTaskManagement.Config.Components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import io.jsonwebtoken.Jwts;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }


    @Test
    void validateToken_withValidToken_shouldReturnTrue() {
        // Arrange
        String username = "testuser";
        String role = "PROJECT_MANAGER";
        String token = jwtUtil.generateToken(username, role);

        // Act
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_withInvalidToken_shouldReturnFalse() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void extractUsername_withValidToken_shouldReturnUsername() {
        // Arrange
        String username = "testuser";
        String role = "PROJECT_MANAGER";
        String token = jwtUtil.generateToken(username, role);

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    void extractRole_withValidToken_shouldReturnRole() {
        // Arrange
        String username = "testuser";
        String role = "PROJECT_MANAGER";
        String token = jwtUtil.generateToken(username, role);

        // Act
        String extractedRole = jwtUtil.extractRole(token);

        // Assert
        assertEquals(role, extractedRole);
    }

    @Test
    void validateToken_withExpiredToken_shouldReturnFalse() {
        String username = "testuser";
        String role = "PROJECT_MANAGER";
        String token = Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1))
                .signWith(Jwts.SIG.HS256.key().build(), Jwts.SIG.HS256)
                .compact();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertFalse(isValid);
    }
}