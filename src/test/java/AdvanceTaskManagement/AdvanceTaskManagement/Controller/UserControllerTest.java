package AdvanceTaskManagement.AdvanceTaskManagement.Controller;

import static org.junit.jupiter.api.Assertions.*;


import AdvanceTaskManagement.AdvanceTaskManagement.Dto.UserDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Handler.GlobalExceptionHandler;
import AdvanceTaskManagement.AdvanceTaskManagement.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private static final String API_BASE_PATH = "/api/users";

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        userDTO = new UserDTO(); // Assuming UserDTO has setters or a builder
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
    }

    @Test
    void getUserById_whenUserExists_shouldReturnOk() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userDTO);

        mockMvc.perform(get(API_BASE_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void getUserById_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
        when(userService.getUserById(1L)).thenThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(get(API_BASE_PATH + "/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_whenUsersExist_shouldReturnOk() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(userDTO));

        mockMvc.perform(get(API_BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    void updateUser_whenValidRequest_shouldReturnOk() throws Exception {
        when(userService.updateUser(eq(1L), any(UserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(put(API_BASE_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateUser_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
        when(userService.updateUser(eq(1L), any(UserDTO.class)))
                .thenThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(put(API_BASE_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_whenIdProvided_shouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete(API_BASE_PATH + "/1"))
                .andExpect(status().isNoContent());
    }
}