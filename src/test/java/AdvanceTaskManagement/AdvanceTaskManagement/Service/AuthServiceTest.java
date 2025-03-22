package AdvanceTaskManagement.AdvanceTaskManagement.Service;

import static org.junit.jupiter.api.Assertions.*;

import AdvanceTaskManagement.AdvanceTaskManagement.Config.Components.JwtUtil;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.User;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.UserRole;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.UserRepository;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.AuthRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.RegisterRequest;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.AuthResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;


    @Test
    void register_whenUserDoesNotExist_shouldRegisterAndReturnToken() {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .name("Test User")
                .username("testuser")
                .password("password")
                .role(UserRole.TEAM_MEMBER.name())
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtUtil.generateToken("test@example.com", UserRole.TEAM_MEMBER.name())).thenReturn("jwtToken");
        when(userRepository.save(Mockito.<User>any())).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0, User.class);
            savedUser.setId(1L);
            return savedUser;
        });

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).encode("password");
        verify(jwtUtil, times(1)).generateToken("test@example.com", UserRole.TEAM_MEMBER.name());
        verify(userRepository, times(1)).save(Mockito.<User>any());
    }

    @Test
    void login_whenCredentialsValid_shouldReturnToken() {
        AuthRequest request = AuthRequest.builder()
                .username("testuser")
                .password("password")
                .build();

        User user = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .email("test@example.com")
                .role(UserRole.TEAM_MEMBER)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("test@example.com", UserRole.TEAM_MEMBER.name())).thenReturn("jwtToken");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password", "encodedPassword");
        verify(jwtUtil, times(1)).generateToken("test@example.com", UserRole.TEAM_MEMBER.name());
    }

    @Test
    void register_whenUsernameExists_shouldThrowException() {
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(User.builder().build()));

        assertThrows(RuntimeException.class, () -> authService.register(request));
        verify(userRepository, times(1)).findByUsername("testuser");
        verifyNoMoreInteractions(passwordEncoder, jwtUtil, userRepository);
    }


    @Test
    void login_whenInvalidPassword_shouldThrowException() {
        AuthRequest request = AuthRequest.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();

        User user = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(request));
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("wrongpassword", "encodedPassword");
        verifyNoInteractions(jwtUtil);
    }
}