package AdvanceTaskManagement.AdvanceTaskManagement.Service;

import static org.junit.jupiter.api.Assertions.*;

import AdvanceTaskManagement.AdvanceTaskManagement.Dto.*;
import AdvanceTaskManagement.AdvanceTaskManagement.Enum.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.*;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.CommentRepository;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.TaskRepository;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.*;
import AdvanceTaskManagement.AdvanceTaskManagement.Request.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User defaultUser;

    @BeforeEach
    void setUp() {
        defaultUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .username("testuser")
                .password("encodedPassword")
                .role(UserRole.TEAM_MEMBER)
                .deleted(false)
                .build();
    }

    @Test
    void getUserById_whenUserExists_shouldReturnUserDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(defaultUser));

        UserDTO response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test User", response.getName());
        assertEquals("test@example.com", response.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_whenUserNotFound_shouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.getUserById(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getAllUsers_whenUsersExist_shouldReturnUserDTOList() {
        when(userRepository.findAll()).thenReturn(List.of(defaultUser));

        List<UserDTO> responses = userService.getAllUsers();

        assertEquals(1, responses.size());
        assertEquals("Test User", responses.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteUser_whenUserExists_shouldMarkAsDeleted() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(defaultUser));
        when(userRepository.save(Mockito.<User>any())).thenAnswer(invocation -> invocation.getArgument(0, User.class));

        userService.deleteUser(1L);

        assertTrue(defaultUser.isDeleted());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(Mockito.<User>any());
    }

    @Test
    void updateUser_whenUserExistsAndNotDeleted_shouldUpdateAndReturnDTO() {
        UserDTO request = UserDTO.builder()
                .id(1L)
                .name("Updated User")
                .email("updated@example.com")
                .username("updateduser")
                .role(UserRole.TEAM_LEADER)
                .password("newPassword")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(defaultUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(Mockito.<User>any())).thenAnswer(invocation -> invocation.getArgument(0, User.class));

        UserDTO response = userService.updateUser(1L, request);

        assertNotNull(response);
        assertEquals("Updated User", response.getName());
        assertEquals("updated@example.com", response.getEmail());
        assertEquals(UserRole.TEAM_LEADER, response.getRole());
        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, times(1)).save(Mockito.<User>any());
    }

    @Test
    void updateUser_whenUserDeleted_shouldThrowException() {
        UserDTO request = UserDTO.builder().name("Updated User").build();
        defaultUser.setDeleted(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(defaultUser));

        assertThrows(IllegalStateException.class, () -> userService.updateUser(1L, request));
        verify(userRepository, times(1)).findById(1L);
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any());
    }
}