package AdvanceTaskManagement.AdvanceTaskManagement.Config.Components;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;

import static org.mockito.Mockito.*;

class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_withValidToken_shouldSetAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "valid-token";
        String authHeader = "Bearer " + token;
        String username = "testuser";
        String role = "PROJECT_MANAGER";

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(jwtUtil.extractRole(token)).thenReturn(role);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, times(1)).validateToken(token);
        verify(jwtUtil, times(1)).extractUsername(token);
        verify(jwtUtil, times(1)).extractRole(token);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        assert authentication.getName().equals(username);
    }

    @Test
    void doFilterInternal_withInvalidToken_shouldPassThrough() throws ServletException, IOException {
        // Arrange
        String token = "invalid-token";
        String authHeader = "Bearer " + token;

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.validateToken(token)).thenReturn(false);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, times(1)).validateToken(token);
        verifyNoMoreInteractions(jwtUtil);
        assert SecurityContextHolder.getContext().getAuthentication() == null;
    }

    @Test
    void doFilterInternal_withNonBearerHeader_shouldPassThrough() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basics");

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
        assert SecurityContextHolder.getContext().getAuthentication() == null;
    }
}